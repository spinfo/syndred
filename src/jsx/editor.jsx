import Draft from 'draft-js';
import React from 'react';

import { debounce, isEqual } from 'lodash';

export default class Editor extends React.Component {

	constructor(props) {
		super(props);

		this.dest = `${window.dest}/editor/`;
		this.parse = debounce(this.parse, 500);
		this.state = {
			editor: Draft.EditorState.createEmpty(),
			focus: false
		};
		this.styleMap = {
			'ERROR': { backgroundColor: 'rgba(255, 0, 0, 0.1' }
		};
	}

	componentDidMount() {
		this.props.socket.subscribe(`${this.dest}pull`,
			(message) => {
				let data = JSON.parse(message.body);
				if (Object.getOwnPropertyNames(data).length == 0) return;

				let cursor = this.state.editor.getSelection();
				let editor = Draft.EditorState
					.push(this.state.editor, Draft.convertFromRaw(data));

				if (cursor.getHasFocus())
					editor = Draft.EditorState.forceSelection(editor, cursor);

				this.setState({ editor }, () =>
					this.editor.refs.editor.contentEditable = true);
			}
		);
	}

	draft(editor) {
		let parse = false
			|| !editor.getCurrentContent()
				.equals(this.state.editor.getCurrentContent())
			|| !editor.getCurrentInlineStyle()
				.equals(this.state.editor.getCurrentInlineStyle())

		this.setState({ editor }, () => parse && this.parse());
	}

	parse() {
		let data = Draft.convertToRaw(this.state.editor.getCurrentContent());

		this.editor.refs.editor.contentEditable = false;
		this.props.socket.send(`${this.dest}push`, {}, JSON.stringify(data));
	}

	render() {
		return (
			<div className='well' onClick={() => this.editor.focus()}>
				{this.renderStyles()}
				<Draft.Editor
					customStyleMap={this.styleMap}
					editorState={this.state.editor}
					onBlur={() => this.setState({ focus: false })}
					onChange={(state) => this.draft(state)}
					onFocus={() => this.setState({ focus: true })}
					ref={(element) => this.editor = element}
					spellCheck={false}
				/>
			</div>
		);
	}

	renderStyles() {
		let styles = ['BOLD', 'ITALIC', 'UNDERLINE'];

		return (
			<div className='btn-group btn-group-sm btn-group-justified'>
				{styles.map((style) => {
					let className = this.state.editor.getCurrentInlineStyle()
						.has(style) ? 'btn btn-primary' : 'btn btn-default';

					let toggle = (event) => {
						event.preventDefault();
						this.draft(Draft.RichUtils
							.toggleInlineStyle(this.state.editor, style));
					};

					return (
						<span
							className={className}
							key={style}
							onMouseDown={(event) => toggle(event)}>
							{style}
						</span>
					);
				})}
			</div>
		);
	}
}
