import * as d3 from 'd3';
import React from 'react';

import { drag } from 'd3-drag';

export default class Parsetree extends React.Component {

	constructor(props) {
        super(props);

        this.depthset = 100;
        this.duration = 500;
    }

    componentDidMount() {
        this.root = d3.select(this.svg).append('g')
            .attr('transform', `translate(0, ${this.depthset})`)
            .attr('data-y', this.depthset);

        this.root.call(d3.drag().on('drag', () => {
            let x = (parseFloat(this.root.attr('data-x')) || 0) + d3.event.dx;
            let y = (parseFloat(this.root.attr('data-y')) || 0) + d3.event.dy;

            this.root.attr('transform', `translate(${x}, ${y})`);
            this.root.attr('data-x', x);
            this.root.attr('data-y', y);
        }));

        let rect = this.root.node().getBoundingClientRect();

        this.root.x = rect.x - this.props.canvas.x;
        this.root.y = rect.y - this.props.canvas.y;

        this.tree = d3.hierarchy(this.props.treeData, (d) => d.children);
        this.tree.children.forEach((d) => this.handleCollapse(d));
        this.tree.x0 = 0, this.tree.y0 = 0;

        this.rend3r(this.tree);
    }

    handleClick(d) {
        if (d.children) {
            d._children = d.children;
            d.children = null;
        } else {
            d.children = d._children;
            d._children = null;
        }

        this.rend3r(d);
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
        let treeSize = [this.props.canvas.height, this.props.canvas.width];
        let treeData = d3.tree().size(treeSize)(this.tree);

        // Compute the new tree layout.
        let nodes = treeData.descendants();
        let links = treeData.descendants().slice(1);

        // Normalize for fixed-depth.
        nodes.forEach((d) => d.y = d.depth * this.depthset);

        // Update the nodes...
        let node = this.root
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
        let link = this.root.selectAll('path.link').data(links, (d) => d.id);

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
