import * as d3 from 'd3';
import React from 'react';

export default class Parsetree extends React.Component {

	constructor(props) {
        super(props);

        this.duration = 500;
        this.depthset = 50;
    }

    componentDidMount() {
        this.canvas = d3.select(this.svg).append('g')
            .attr('transform', `translate(0, ${this.depthset})`);

        this.root = d3.hierarchy(this.props.treeData, (d) => d.children);
        this.root.children.forEach((d) => this.handleCollapse(d));
        this.root.x0 = 0, this.root.y0 = 0;
    }

    componentDidUpdate() {
        console.log('that', this.canvas)
        console.log('full', this.props.canvasWidth / 2)
        console.log('elem', this.canvas.node().getBBox().width)
        console.log('bbox', this.canvas.node().getBBox())
        console.log('cbox', this.canvas.node().getBoundingClientRect())

        // let x = this.props.canvasWidth / 2 - this.canvas.node().getBBox().width / 2;
        // console.log('x', x)

        this.canvas.attr('transform', `translate(0, ${this.depthset})`);
        this.rend3r(this.root);
    }

    handleClick(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }

        // this.rend3r(d);
        this.forceUpdate();
    }

    handleCollapse(d) {
        if (d.children) {
            d._children = d.children;
            d._children.forEach((d) => this.handleCollapse(d));
            d.children = null;
        }
    }

    handleDiagonal(s, d) {
        return `M ${s.x} ${s.y} C ${s.x} ${(s.y + d.y) / 2},
            ${d.x} ${(s.y + d.y) / 2}, ${d.x} ${d.y}`;
    }

    render() {
        return (<svg ref={(element) => this.svg = element} />);
    }

    rend3r(ds) {
        // Assigns the x and y position for the nodes
        let treeData = d3.tree()
            .size([this.props.canvasHeight, this.props.canvasWidth])(this.root);

        // Compute the new tree layout.
        let nodes = treeData.descendants();
        let links = treeData.descendants().slice(1);

        // Normalize for fixed-depth.
        nodes.forEach((d) => d.y = d.depth * this.depthset);

        // Update the nodes...
        let node = this.canvas
            .selectAll('g.node')
            .data(nodes, (d) =>
                d.id || (d.id = Math.random().toString(36).substring(2)));

        // Enter any new modes at the parent's previous position.
        let nodeEnter = node.enter()
            .append('g')
            .attr('class', 'node')
            .attr('transform', (d) => `translate(${ds.x0}, ${ds.y0})`)
            .on('click', (d) => this.handleClick(d));

        // Add Circle for the nodes
        nodeEnter
            .append('circle')
            .attr('class', 'node')
            .attr('r', 1e-6)
            .style('fill', (d) => d._children ? 'lightsteelblue' : '#fff');

        // Add labels for the nodes
        nodeEnter
            .append('text')
            .attr('dy', '.35em')
            .attr('x', (d) => d.children || d._children ? -13 : 13)
            .attr('text-anchor',
                (d) => d.children || d._children ? 'end' : 'start')
            .text((d) => d.data.name);

        nodeEnter
            .append('text')
            .attr('dy', '1.5em')
            .attr('x', (d) => d.children || d._children ? -13 : 13)
            .attr('fill', 'green')
            .attr('text-anchor',
                (d) => d.children || d._children ? 'end' : 'start')
            .text((d) => d.data.match);

        // UPDATE
        let nodeUpdate = nodeEnter.merge(node);

        // Transition to the proper position for the node
        nodeUpdate
            .transition().duration(this.duration)
            .attr('transform', (d) => `translate(${d.x}, ${d.y})`);

        // Update the node attributes and style
        nodeUpdate
            .select('circle.node')
            .attr('r', 10)
            .style('fill', (d) => d._children ? 'lightsteelblue' : '#fff')
            .attr('cursor', 'pointer');

        // Remove any exiting nodes
        let nodeExit = node.exit()
            .transition().duration(this.duration)
            .attr('transform', (d) => `translate(${ds.x}, ${ds.y})`)
            .remove();

        // On exit reduce the node circles size to 0
        nodeExit.select('circle').attr('r', 1e-6);

        // On exit reduce the opacity of text labels
        nodeExit.select('text').style('fill-opacity', 1e-6);

        // Update the links...
        let link = this.canvas.selectAll('path.link').data(links, (d) => d.id);

        // Enter any new links at the parent's previous position.
        let linkEnter = link.enter()
            .insert('path', 'g')
            .attr('class', 'link')
            .attr('d', (d) => this.handleDiagonal(
                { x: ds.x0, y: ds.y0 },
                { x: ds.x0, y: ds.y0 }
            ));

        // UPDATE
        let linkUpdate = linkEnter.merge(link);

        // Transition back to the parent element position
        linkUpdate
            .transition().duration(this.duration)
            .attr('d', (d) => this.handleDiagonal(d, d.parent));

        // Remove any exiting links
        let linkExit = link.exit()
            .transition().duration(this.duration)
            .attr('d', (d) => this.handleDiagonal(
                { x: ds.x, y: ds.y },
                { x: ds.x, y: ds.y }
            ))
            .remove();

        // Store the old positions for transition.
        nodes.forEach((d) => Object.assign(d, { x0: d.x }, { y0: d.y }));
    }

}
