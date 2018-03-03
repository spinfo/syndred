import React from 'react';
import Textarea from 'react-textarea-autosize';

export default class Parser extends React.Component {

	get parserMap() {
		return {
			rbnf: 'Rich Backus–Naur form (RBNF)',
			regex: 'Regular Expressions',
			test: 'Simple echo test'
		};
	}

	constructor(props) {
		super(props);

		this.dest = `${window.dest}/parser`;
		this.state = {
			name: '',
			grammar: '',
			error: '',
			running: false
		};
	}

	componentDidMount() {
		this.props.socket.subscribe(`${this.dest}/get`,
			(message) => this.setState(JSON.parse(message.body),
				() => this.props.run()));
	}

	setParser(event) {
		event.preventDefault();
		this.props.socket
			.send(`${this.dest}/set`, { }, JSON.stringify(this.state));
	}

	render() {
		return (
			<form id='parser' className='form-horizontal well'>
				<fieldset>
					<legend>Parser</legend>
					{this.state.error && this.renderError()}

					<div className={'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')}>
						<label className='col-lg-2 control-label'>
							Gramma
						</label>
						<div className='col-lg-10'>
							<Textarea
								className='form-control'
								onChange={(event) => this.setState({
									grammar: event.target.value
								})}
								minRows={5}
								style={{ resize: 'vertical' }}
								value={this.state.grammar}
							/>
						</div>
					</div>

					<div className={'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')}>
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
								{Object.keys(this.parserMap).map((name) => (
									<option key={name} value={name}>
										{this.parserMap[name]}
									</option>
								))}
							</select>
						</div>
					</div>

					<div className={'form-group'
						+ (this.state.error ? ' has-error' : '')
						+ (this.state.running ?' has-success' : '')}>
						<label className='col-lg-2 control-label'>
							Status
						</label>
						<div className='col-lg-4'>
							<button
								className='btn btn-block btn-primary'
								onClick={(event) => this.setParser(event)}>
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
