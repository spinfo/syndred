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

	// componentWillUnmount() {
	// 	if (this.state.socket !== null) {
	// 		this.state.socket.disconnect(() => {
	// 			this.setState({ ready: false, socket: null });
	// 		});
	// 	}
	// }

	render() {
		return (
			<div className='row'>
				<div className='col-md-5'>
					{ this.state.ready &&
						<ParserComponent socket={this.state.socket} />
					}
				</div>
				<div className='col-md-7'>
					{ this.state.ready &&
						<EditorComponent socket={this.state.socket} />
					}
				</div>
			</div>
		);
	}

}

ReactDOM.render(<AppComponent />, document.getElementById('app'));
