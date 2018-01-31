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
			ready: false,
			socket: Stomp.over(new SockJS('/websocket'))
		};
		this.state.socket.debug = null;
	}

	componentDidMount() {
		window.setTimeout(() => this.componentDidMount(), 2500);

		if (!this.state.ready)
		this.state.socket.connect({ instance: location.hash },
			() => this.setState({ ready: true }),
			() => this.setState({ ready: false }));
		}

	componentWillMount() {
		if (!location.hash)
			location.hash = Math.random().toString(36).substring(2);

		window.dest = '/syndred/' + location.hash;
	}

	render() {
		return this.state.ready ? (
			<div className='row'>
				<div className='col-md-5'>
					<Parser socket={this.state.socket} />
				</div>
				<div className='col-md-7'>
					<Editor socket={this.state.socket} />
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
