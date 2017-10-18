'use strict';

class AppComponent extends React.Component {

	state = {
		ready: false,
		socket: Stomp.over(new SockJS('/websocket'))
	};

	constructor(props) {
		super(props);
		$(window).on('hashchange', () => { location.reload(); });

    this.state.socket.connect({}, (frame) => {
			this.setState({ ready: true });
    });
	}

	render() {
		return this.state.ready ? (
			<div className='row'>
				<div className='col-md-5'>
						<ParserComponent socket={this.state.socket} />
				</div>
				<div className='col-md-7'>
						<EditorComponent socket={this.state.socket} />
				</div>
			</div>
			)	: (
				<div className='row'>
					<div className='col-md-12 text-center'>
						<img class="spinner" src="/spinner.svg" />
						<h1><strong>Connecting</strong></h1>
					</div>
				</div>
			)
	}
}

ReactDOM.render(<AppComponent />, document.getElementById('app'));
