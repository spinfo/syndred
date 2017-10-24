'use strict';

class ParserComponent extends React.Component {

	state = {
		name: '',
		gramma: '',
		error: '',
		running: false
	};

	componentWillMount() {
		this.props.socket.subscribe('/syndred/' + location.hash + '/parser',
			(message) => this.setState(JSON.parse(message.body))
		);
	}

	setParser(event) {
		event.preventDefault();
		let json = JSON.stringify(this.state);
		this.props.socket.send('/syndred/' + location.hash + '/parser', {}, json);
	}

	render() {
		return (
			<form id="parser" className="form-horizontal well">
				<fieldset>
					<legend>Parser</legend>
					{this.state.error &&
						<div className="alert alert-dismissible alert-danger">
							<button type="button" className="close" onClick={() =>
								this.setState({error:''})}>&times;</button>{this.state.error}
						</div>
					}
					<div className={"form-group"+(this.state.error?" has-error":"")+
						(this.state.running?" has-success":"")}>
						<label for="parser-gramma" className="col-lg-2 control-label">
							Gramma</label>
						<div className="col-lg-10">
							<textarea id="parser-gramma" className="form-control" rows="5"
								onChange={() => this.state.gramma=$('#parser-gramma').val() }>
								{this.state.gramma}</textarea>
						</div>
					</div>
					<div className={"form-group"+(this.state.error?" has-error":"")+
						(this.state.running?" has-success":"")}>
						<label for="parser-name" className="col-lg-2 control-label">
							Type</label>
						<div className="col-lg-10">
							<select id="parser-name" className="form-control"
								onChange={() => this.state.name = $('#parser-name').val() }>
								<option selected={this.state.name === 'abnf'} value="abnf">
									Augmented Backus–Naur form (ABNF)</option>
								<option selected={this.state.name === 'ebnf'} value="ebnf">
									Extended Backus–Naur form (EBNF)</option>
								<option selected={this.state.name === 'regex'} value="regex">
									Regular Expressions</option>
							</select>
						</div>
					</div>
					<div className={"form-group"+(this.state.error?" has-error":"")+
						(this.state.running?" has-success":"")}>
						<label for="parser-name" className="col-lg-2 control-label">
							Status</label>
						<div className="col-lg-10">
							<button type="submit" className="btn btn-primary"
								onClick={(event) => this.setParser(event)}>Apply</button>
						</div>
					</div>
				</fieldset>
			</form>
		);
	}

}
