'use strict';

class AppComponent extends React.Component {

	state = {
		parser: null,
		ready: false,
		socket: Stomp.over(new SockJS('/websocket'))
	};

	constructor(props) {
		super(props);

    this.state.socket.connect({}, (frame) => {
			this.setState({ ready: true });
      // this.state.socket.subscribe('/syndred/parser/id', (e) => {
			// 	this.setState({ ready: e });
			// 	console.log(e);
      // });
    });

	}

	componentWillUnmount() {
		if (this.state.socket !== null) {
			this.state.socket.disconnect(() => {
				this.setState({ ready: false, socket: null });
			});
		}
	}

	render() {
		return (
			<div className='row'>
				<div className='col-md-5'><ParserComponent
					ready={this.state.ready}
					setParser={(e) => this.setParser(e)}
				/></div>
				<div className='col-md-7'><EditorComponent
				/></div>
			</div>
		);
	}

	setParser(parser) {
		let json = JSON.stringify(parser);
		this.state.socket.send('/syndred/parser/id', {}, json);
	}

}

ReactDOM.render(<AppComponent />, document.getElementById('app'));
