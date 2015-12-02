import React, { Component } from 'react';
import { createHistory, useBasename } from 'history';
import { Router, Route, Link, History } from 'react-router'
import config from '../config.json';
import * as _ from 'lodash';

export class App extends Component {
    constructor(props) {
        super(props);
    }

    state = {
        items: [],
        searchText: "",
        loaded: true
    };

    _searchFetch(text) {
        var self = this;
        fetch('/search/' + encodeURIComponent(text))
            .then(res => {
                return res.json()
            })
            .then(function (json) {
                console.log(json);
                self.setState({
                    items: json.items,
                    loaded: true
                })
            })
            .catch(function (ex) {
                console.error('parsing failed', ex);
                self.setState({
                    items: [],
                    loaded: true
                })
            });
    }

    search = _.debounce((text) => {
        this._searchFetch(text);
    }, 1000);

    searchTextChanged = (e) => {
        var searchText = e.target.value;
        this.setState({
            searchText: searchText,
            loaded: false
        }, function () {
            if (searchText)
                this.search(searchText);
        });
    };

    render() {
        function displayOwner(owner) {
            owner = owner || {};
            return (
                <a href={owner.link} target="_blank">{owner.display_name}</a>
            );
        }

        function displayDate(item) {
            return new Date(item.creation_date * 1000).toGMTString();
        }

        function displayTags(tags) {
            /* tags: Array[3]*/
            return "";
        }

        var rows = this.state.items.map(function (item) {
            return <tr key={item.question_id} className={item.is_answered ? "answered": "not-answered"}>
                <td>{displayDate(item) + ""}</td>
                <td>{displayOwner(item.owner)}</td>
                <td><a href={item.link} target="_blank">{item.title}</a></td>
            </tr>
        });

        return (
            <div className="container">
                <h2>StackOverflow Finder</h2>

                <input type="search" className="form-control" value={this.state.searchText}
                       onChange={this.searchTextChanged} placeholder="search for..."/>

                {this.state.loaded ? null :
                    <div className="panel panel-default">
                        <div className="panel-body">
                            Loading...
                        </div>
                    </div>
                }

                <table className="table table-strip">
                    <thead>
                    <tr>
                        <th className="date-col">Date</th>
                        <th className="owner-col">Owner</th>
                        <th className="title-col">Title</th>
                    </tr>
                    </thead>
                    <tbody>
                    {rows}
                    </tbody>
                </table>
            </div>
        )
    }
}