'use strict';

class ParserComponent extends React.Component {

	state = {
		name: 'ebnf',
		gramma: ''
	};

	// constructor(props) {
	// 	super(props);
	// }

	handleClick(event) {
		event.preventDefault();

		let forms = $('#parser .form-group:first-of-type');
		let error = this.props.setParser(this.state);

		if (error) forms.removeClass('has-error');
		else forms.addClass('has-error');
	}

	render() {
		return (
			<form id="parser" className="form-horizontal well">
				<fieldset>
					<legend>Parser</legend>
					<div className="form-group">
						<label for="parser-gramma" className="col-lg-2 control-label">
							Gramma
						</label>
						<div className="col-lg-10">
							<textarea disabled={!this.props.ready} className="form-control"
								rows="5" id="parser-gramma">{this.state.gramma}
							</textarea>
						</div>
					</div>
					<div className="form-group">
						<label for="parser-name" className="col-lg-2 control-label">Type
						</label>
						<div className="col-lg-10">
							<select disabled={!this.props.ready}
								className="form-control" id="parser-name">

								<option selected={this.state.name === 'regex'} value="regex">
									Regular Expressions
								</option>
								<option selected={this.state.name === 'ebnf'} value="ebnf">
									Extended Backus–Naur form (EBNF)
								</option>
								<option selected={this.state.name === 'abnf'} value="abnf">
									Augmented Backus–Naur form (ABNF)
								</option>
							</select>
						</div>
					</div>
					<div className="form-group">
						<div className="col-lg-10 col-lg-offset-2">
							<button disabled={!this.props.ready} onClick={(e) => this.handleClick(e)}
								type="submit" className="btn btn-primary">Apply</button>
						</div>
					</div>
				</fieldset>
			</form>
		);
	}

}
