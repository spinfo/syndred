'use strict';

const {convertFromRaw, convertToRaw, Editor, EditorState, RichUtils} = Draft;

class Syndred extends React.Component {

	submitCounter = null;

	constructor(props) {
		super(props);
		this.state = {editorState: EditorState.createEmpty()};

		this.onChange = (editorState) => this.setState({editorState});
		// this.onChange = (editorState) => this.handleChange(editorState);

		this.focus = () => this.refs.editor.focus();
		this.onTab = (e) => this.handleTab(e);
		this.handleKeyCommand = (command) => this.handleKey(command);

		this.toggleBlockType = (type) => this.switchBlock(type);
		this.toggleInlineStyle = (style) => this.switchInline(style);
	}

	handleChange(editorState) {
		this.setState({editorState});

		if (this.submitCounter) window.clearTimeout(this.submitCounter);
		this.submitCounter = window.setTimeout(() => this.handleSubmit(), 750);
	}

	handleSubmit() {
		const {editorState} = this.state;
		if (this.submitCounter) window.clearTimeout(this.submitCounter);
		$('.public-DraftEditor-content').prop('contenteditable', 'false');

		$.ajax({
			async: true,
			url: '/edit',
			type: 'POST',
			dataType : 'json',
			data: JSON.stringify(convertToRaw(editorState.getCurrentContent())),
			contentType : 'application/json; charset=utf-8',
		}).done(jQuery.proxy(function(raw) {
			let data = convertFromRaw(raw);
			let news = EditorState.push(editorState, data, 'spellcheck-change');
			this.onChange(EditorState.moveFocusToEnd(news));
    }, this));

		$('.public-DraftEditor-content').prop('contenteditable', 'true');
	}

	handleKey(command) {
		const {editorState} = this.state;
		const newState = RichUtils.handleKeyCommand(editorState, command);

		if (this.submitCounter) window.clearTimeout(this.submitCounter);
		this.submitCounter = window.setTimeout(() => this.handleSubmit(), 750);

		if (newState) {
			this.onChange(newState);
			return true;
		}
		return false;
	}

	handleTab(e) {
		const maxDepth = 4;
		this.onChange(
			RichUtils.onTab(e, this.state.editorState, maxDepth)
		);
	}

	switchBlock(blockType) {
		this.onChange(
			RichUtils.toggleBlockType(this.state.editorState, blockType)
		);
	}

	switchInline(inlineStyle) {
		this.onChange(
			RichUtils.toggleInlineStyle(this.state.editorState, inlineStyle)
		);
	}

	render() {
		const {editorState} = this.state;
		let className = 'syndred-editor';

		return (
			<div className="syndred-root">
				<BlockStyleControls
					editorState={editorState}
					onToggle={this.toggleBlockType}
				/>
				<InlineStyleControls
					editorState={editorState}
					onToggle={this.toggleInlineStyle}
				/>
				<div className={className} onClick={this.focus}>
					<Editor
						readOnly={this.state.readOnly}
						blockStyleFn={getBlockStyle}
						customStyleMap={styleMap}
						editorState={editorState}
						handleKeyCommand={this.handleKeyCommand}
						onChange={this.onChange}
						onTab={this.onTab}
						ref="editor"
						spellCheck={false}
					/>
				</div>
			</div>
		);
	}
}

// Custom overrides for "code" style.
const styleMap = {
	CODE: {
		backgroundColor: 'rgba(0, 0, 0, 0.05)',
		fontFamily: '"Inconsolata", "Menlo", "Consolas", monospace',
		fontSize: 16,
		padding: 2,
	},
};

function getBlockStyle(block) {
	switch (block.getType()) {
		case 'blockquote': return 'syndred-blockquote';
		default: return null;
	}
}

class StyleButton extends React.Component {
	constructor() {
		super();
		this.onToggle = (e) => {
			e.preventDefault();
			this.props.onToggle(this.props.style);
		};
	}

	render() {
		let className = 'syndred-styleButton';
		if (this.props.active) {
			className += ' syndred-activeButton';
		}

		return (
			<span className={className} onMouseDown={this.onToggle}>
			{this.props.label}
			</span>
		);
	}
}

const BLOCK_TYPES = [
	{label: 'H1', style: 'header-one'},
	{label: 'H2', style: 'header-two'},
	{label: 'H3', style: 'header-three'},
	{label: 'H4', style: 'header-four'},
	{label: 'H5', style: 'header-five'},
	{label: 'H6', style: 'header-six'},
	{label: 'Blockquote', style: 'blockquote'},
	{label: 'UL', style: 'unordered-list-item'},
	{label: 'OL', style: 'ordered-list-item'},
	{label: 'Code Block', style: 'code-block'},
];

const BlockStyleControls = (props) => {
	const {editorState} = props;
	const selection = editorState.getSelection();
	const blockType = editorState
	.getCurrentContent()
	.getBlockForKey(selection.getStartKey())
	.getType();

	return (
		<div className="syndred-controls">
		{BLOCK_TYPES.map((type) =>
			<StyleButton
			key={type.label}
			active={type.style === blockType}
			label={type.label}
			onToggle={props.onToggle}
			style={type.style}
			/>
		)}
		</div>
	);
};

var INLINE_STYLES = [
	{label: 'Bold', style: 'BOLD'},
	{label: 'Italic', style: 'ITALIC'},
	{label: 'Underline', style: 'UNDERLINE'},
	{label: 'Monospace', style: 'CODE'},
	{label: 'Parsed', style: 'PARSED'},
];

const InlineStyleControls = (props) => {
	var currentStyle = props.editorState.getCurrentInlineStyle();
	return (
		<div className="syndred-controls">
		{INLINE_STYLES.map(type =>
			<StyleButton
				key={type.label}
				active={currentStyle.has(type.style)}
				label={type.label}
				onToggle={props.onToggle}
				style={type.style}
			/>
		)}
		</div>
	);
};

ReactDOM.render(<Syndred />, document.getElementById('syndred'));
