import React from 'react';
import Textarea from 'react-textarea-autosize';

export default class Parser extends React.Component {

	constructor(props) {
		super(props);

		this.dest = `${window.dest}/parser/`;
		this.state = {
			name: '',
			grammar: '',
			error: '',
			running: false
		};
	}

	componentDidMount() {
		this.props.socket.subscribe(`${this.dest}pull`,
			(message) => this.setState(JSON.parse(message.body))
		);
	}

	setParser(event) {
		event.preventDefault();
		this.props.socket
			.send(`${this.dest}push`, {}, JSON.stringify(this.state));
	}

	render() {
		return (
			<form id='parser' className='form-horizontal well'>
				<fieldset>
					<legend>Parser</legend>
					{this.state.error && this.renderError()}

					<div className={
						'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')
					}>
						<label className='col-lg-2 control-label'>
							Gramma
						</label>
						<div className='col-lg-10'>
							<Textarea
								className='form-control'
								onChange={(event) => this.setState({
									grammar: event.target.value
								}, () => console.log(event))}
								minRows={5}
								value={this.state.grammar}
							/>
						</div>
					</div>

					<div className={
						'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')
					}>
						<label className='col-lg-2 control-label'>
							Type
						</label>
						<div className='col-lg-10'>
							<select
								id='parser-name'
								className='form-control'
								onChange={(event) => this.setState({
									name: event.target.value
								})}
								value={this.state.name}>
								<option value='abnf'>
									Augmented Backus–Naur form (ABNF)
								</option>
								<option value='rbnf'>
									Rich Backus–Naur form (RBNF)
								</option>
								<option value='regex'>
									Regular Expressions
								</option>
								<option value='test'>
									Simple echo test
								</option>
							</select>
						</div>
					</div>

					<div className={
						'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')
					}>
						<label className='col-lg-2 control-label'>
						Status
						</label>
						<div className='col-lg-4'>
							<button
								className='btn btn-block btn-primary'
								onClick={(event) => this.setParser(event)}
								type='submit' >
								Apply
							</button>
						</div>
					</div>
				</fieldset>
			</form>
		);
	}

	renderError() {
		return (
			<div className='alert alert-dismissible alert-danger'>
				<button
					type='button'
					className='close'
					onClick={() => this.setState({ error: '' })}>
				&times;
				</button>
				{this.state.error}
			</div>
		);
	}

}
