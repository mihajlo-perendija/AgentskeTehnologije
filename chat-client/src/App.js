import React, { Component } from 'react';
import './App.css';
import { HashRouter as Router, Route } from 'react-router-dom'
import Register from './components/register/Register.js'
import Login from './components/login/Login.js'
import Conversations from './components/conversations/Conversations.js'
import Conversation from './components/conversation/Conversation.js'

class App extends Component {
    state = {
        loggedInUser: {
            id: 5,
            username: "Mihajlo Perendija"
        },
        users: [
            {
                id: 1,
                username: "Che Guevara",
                messages: [{
                    sender: "Che Guevara",
                    reciever: "Mihajlo Perendija",
                    text: "Hola camarada!",
                    date: new Date(1587148000000)
                },
                ]
            },
            {
                id: 2,
                username: "Vladimir Iljic Uljanov Lenjin",
                messages: [{
                    sender: "Vladimir Iljic Uljanov Lenjin",
                    reciever: "Mihajlo Perendija",
                    text: "Привет, товарищ!",
                    date: new Date(1587148000000)
                },
                {
                    sender: "Mihajlo Perendija",
                    reciever: "Vladimir Iljic Uljanov Lenjin",
                    text: "Поздрав друже!",
                    date: new Date(1587148716000)
                },
                ]
            },
            {
                id: 3,
                username: "Karl Marx",
                messages: [{
                    sender: "Karl Marx",
                    reciever: "Mihajlo Perendija",
                    text: "Hallo Kamerade!",
                    date: new Date(1587148000000)
                },
                ]
            },
            {
                id: 4,
                username: "Lav Trocki",
                messages: [{
                    sender: "Lav Trocki",
                    reciever: "Mihajlo Perendija",
                    text: "Привет, товарищ!",
                    date: new Date(1587148000000)
                },
                ]
            }
        ],
        selectedUser: {
            id: 2,
            username: "Vladimir Iljic Uljanov Lenjin",
            messages: [{
                sender: "Vladimir Iljic Uljanov Lenjin",
                reciever: "Mihajlo Perendija",
                text: "Привет, товарищ!",
                date: new Date(1587148000000)
            },
            {
                sender: "Mihajlo Perendija",
                reciever: "Vladimir Iljic Uljanov Lenjin",
                text: "Поздрав друже!",
                date: new Date(1587148716000)
            },
            ]
        }
    }

    selectConversation = (user) => {
        this.setState({ selectedUser: user });
    }

    componentDidMount() {
        //this.setState({ selectedUser: this.state.users[0] });
    }

    render() {
        return (
            <Router>
                <div className="App">
                    <Route exact path="/" render={props => (
                        <React.Fragment>
                            <Login></Login>
                        </React.Fragment>
                    )} />
                    <Route exact path="/login" render={props => (
                        <React.Fragment>
                            <Login></Login>
                        </React.Fragment>
                    )} />
                    <Route exact path="/register" render={props => (
                        <React.Fragment>
                            <Register></Register>
                        </React.Fragment>
                    )} />
                    <Route exact path="/chat" render={props => (
                        <React.Fragment>
                            <div id="chat-container">
                                <div id="search-container">
                                    <input type="text" placeholder="Search" />
                                </div>
                                <div id="conversation-list">
                                    <Conversations users={this.state.users} selectConversation={this.selectConversation} selectedUser={this.state.selectedUser}></Conversations>
                                </div>
                                <div id="new-message-container">

                                </div>
                                <div id="chat-title">
                                    <span>{this.state.selectedUser?.username}</span>
                                </div>
                                <div id="chat-message-list">
                                    <Conversation user={this.state.selectedUser} loggedInUser={this.state.loggedInUser}></Conversation>
                                </div>
                                <div id="chat-form">
                                    <input type="text" placeholder="type a message" />
                                </div>

                            </div>
                        </React.Fragment>
                    )} />
                </div>
            </Router>
        );
    }
}

export default App;
