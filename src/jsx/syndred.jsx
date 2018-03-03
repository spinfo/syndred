import React from 'react';
import ReactDOM from 'react-dom';
import SockJS from 'sockjs-client';
import Stomp from '@stomp/stompjs';

import Editor from './editor';
import Parser from './parser';

class Syndred extends React.Component {

	constructor(props) {
		super(props);

		this.state = {
			ready: false
		};
	}

	componentDidMount() {
		window.setTimeout(() => this.componentDidMount(), 2500);

		if (!this.socket) {
			this.socket = Stomp.over(new SockJS('/websocket'));
			this.socket.debug = () => null;
		}

		if (!this.state.ready) {
			this.socket.connect({ id: location.hash },
				() => this.setState({ ready: true }),
				() => this.setState({ ready: false }));
		}
	}

	componentWillMount() {
		window.dest = '/syndred/' + window.location.hash;
		window.onhashchange = () => window.location.reload();

		if (!window.location.hash) {
			this.componentDidMount = null;
			window.location.hash = Math.random().toString(36).substring(2);
		}
	}

	render() {
		return this.state.ready ? (
			<div className='row'>
				<div className='col-md-5'>
					<Parser
						run={() => this.editor.parse()}
						socket={this.socket}
					/>
				</div>
				<div className='col-md-7'>
					<Editor
						ref={(ref) => this.editor = ref}
						socket={this.socket}
					/>
				</div>
			</div>
		) : (
			<div className='row'>
				<div className='col-md-12 text-center'>
					<img className='spinner' src='/spinner.svg' />
					<h1><strong>Connecting</strong></h1>
				</div>
			</div>
		);
	}

}

ReactDOM.render(<Syndred />, document.getElementById('syndred'));
