'use strict';

class EditorComponent extends React.Component {

	state = {editorState: Draft.EditorState.createEmpty()};

	componentWillMount() {
		this.props.socket.subscribe('/syndred/' + location.hash + '/editor',
			(message) => {
				let raw = JSON.parse(message.body);
				if (jQuery.isEmptyObject(raw)) return;

				let selection = this.state.editorState.getSelection();
				let editorState = Draft.EditorState.push(
					this.state.editorState, Draft.convertFromRaw(raw));

				if (selection.getHasFocus()) editorState =
					Draft.EditorState.forceSelection(editorState, selection);

				this.setState({editorState});
			}
		);
	}

	parseState() {
		clearTimeout(window.cooldown);
		let raw = Draft.convertToRaw(this.state.editorState.getCurrentContent());
		let json = JSON.stringify(raw);
		this.props.socket.send('/syndred/' + location.hash + '/editor', {}, json);
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
							this.setState({editorState});
						}}>{style}</span>);
					})}
				</div>
				<Draft.Editor ref="editor"
					editorState={this.state.editorState}
					onChange={(editorState) => {
						if (window.cooldown) clearTimeout(window.cooldown);
						window.cooldown = setTimeout(() => this.parseState(), 500);
						this.setState({editorState});
					}}
					spellCheck={false}
				/>
			</div>
		);
	}
}
