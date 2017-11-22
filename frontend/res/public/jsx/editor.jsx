'use strict';

const styleMap = {
		'error': {
			text-decoration: 'line-through',
		},
	}

class EditorComponent extends React.Component {

	state = {editorState: Draft.EditorState.createEmpty()};

	componentWillMount() {
		this.props.socket.subscribe('/syndred/'+location.hash+'/editor/pull',
			(message) => {
				let raw = JSON.parse(message.body);
				if (jQuery.isEmptyObject(raw)) return;

				let selection = this.state.editorState.getSelection();
				let editorState = Draft.EditorState.push(
					this.state.editorState, Draft.convertFromRaw(raw));

				if (selection.getHasFocus()) editorState =
					Draft.EditorState.forceSelection(editorState, selection);

				this.setState({editorState});
				window.conplain = editorState.getCurrentContent().getPlainText();
				$('div[role="textbox"]').prop('contenteditable', 'true');
			}
		);
	}

	parseState(force = false) {
		let conplain = this.state.editorState.getCurrentContent().getPlainText();
		if (!force && window.conplain === conplain) return;

		let raw = Draft.convertToRaw(this.state.editorState.getCurrentContent());
		let json = JSON.stringify(raw);

		window.conplain = conplain;
		$('div[role="textbox"]').prop('contenteditable', 'false');
		this.props.socket.send('/syndred/'+location.hash+'/editor/push', {}, json);
	}

	render() {
		return (
			<div className="well" onClick={() => this.refs.editor.focus()}>
				<div class="btn-group btn-group-sm btn-group-justified">
					{['BOLD', 'ITALIC', 'UNDERLINE', 'CODE'].map((style) => {
						let editorState = this.state.editorState;
						let className = editorState.getCurrentInlineStyle().has(style)
							? 'btn btn-primary' : 'btn btn-default';

						editorState = Draft.RichUtils.toggleInlineStyle(editorState, style);
						return (<span className={className} onMouseDown={(event) => {
							event.preventDefault();
							this.setState({editorState}, () => this.parseState(true));
						}}>{style}</span>);
					})}
				</div>
				<Draft.Editor ref="editor"

					customStyleMap = {styleMap}
					editorState={this.state.editorState}
					onChange={(editorState) => {
						this.setState({editorState});
						if (window.cooldown) clearTimeout(window.cooldown);
						window.cooldown = setTimeout(() => this.parseState(), 500);
					}}
					spellCheck={false}
				/>
			</div>
		);
	}
}
