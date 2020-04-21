import React, { Component } from 'react';
import { HashRouter as Router, Route, Link } from 'react-router-dom'
import { Persist } from 'react-persist'
import './App.css';
import Register from './components/register/Register.js'
import Login from './components/login/Login.js'
import Conversations from './components/conversations/Conversations.js'
import Conversation from './components/conversation/Conversation.js'

class App extends Component {
    state = {
        loggedInUser: null,
        users: [],
        selectedUser: {
            // id: 2,
            // username: "Vladimir Iljic Uljanov Lenjin",
            // messages: [{
            //     sender: "Vladimir Iljic Uljanov Lenjin",
            //     reciever: "Mihajlo Perendija",
            //     text: "Привет, товарищ!",
            //     date: new Date(1587148000000)
            // },
            // {
            //     sender: "Mihajlo Perendija",
            //     reciever: "Vladimir Iljic Uljanov Lenjin",
            //     text: "Поздрав друже!",
            //     date: new Date(1587148716000)
            // },
            // ]

        },
        text: "",
        visibleMessages: []
    }

    selectConversation = (user) => {
        this.setState({
            selectedUser: user,
            text: ""
        }, this.getConversationMessages);
    }

    getConversationMessages = () => {
        if (this.state.selectedUser.username === "ALL") {
            let messages = this.state.loggedInUser.messages
                .filter(message => message.reciever === "ALL");

            this.setState({ visibleMessages: messages });
        } else {
            let messages = this.state.loggedInUser.messages
                .filter(message => (message.sender === this.state.selectedUser.username
                    && message.reciever === this.state.loggedInUser.username)
                    ||
                    (message.sender === this.state.loggedInUser.username
                    && message.reciever === this.state.selectedUser.username));

            this.setState({ visibleMessages: messages });
        }
    }

    setLoggedInUser = (user) => {
        this.setState({ loggedInUser: user });
    }

    logout = () => {
        const url = process.env.NODE_ENV === 'production' ? `rest/chat/users/loggedIn/${this.state.loggedInUser.username}` : `http://localhost:8080/ChatWar/rest/chat/users/loggedIn/${this.state.loggedInUser.username}`;
        fetch(url, { method: "DELETE", headers: { "content-type": "application/json" } })
            .then((response) => {
                if (!response.ok) {
                    alert("Error");
                }
                else {
                    this.setState({
                        loggedInUser: null,
                        selectedUser: null,
                        users: []
                    });
                }
            })
            .catch((error) => {
                console.log(error);
            });

    }

    getOnlineUsers = () => {
        const url = process.env.NODE_ENV === 'production' ? "rest/chat/users/loggedIn" : "http://localhost:8080/ChatWar/rest/chat/users/loggedIn";
        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    alert("Error");
                }
                else {
                    return response.json();
                }
            }).then((data) => {
                this.setState({ users: data.filter(user => user.username !== this.state.loggedInUser.username) });
            })
            .catch((error) => {
                console.log(error);
            });
    }

    getAllUsers = () => {
        const url = process.env.NODE_ENV === 'production' ? "rest/chat/users/registered" : "http://localhost:8080/ChatWar/rest/chat/users/registered";
        fetch(url)
            .then((response) => {
                if (!response.ok) {
                    alert("Error");
                }
                else {
                    return response.json();
                }
            }).then((data) => {
                this.setState({ users: data.filter(user => user.username !== this.state.loggedInUser.username) });
            })
            .catch((error) => {
                console.log(error);
            });
    }



    onMessageChange = (e) => {
        this.setState({ text: e.target.value });
    }

    onSendMessage = (e) => {
        e.preventDefault();
        let type;
        if (this.state.selectedUser.username === "ALL") {
            type = "all";
        } else {
            type = "user";
        }
        let message = {
            sender: this.state.loggedInUser.username,
            reciever: this.state.selectedUser.username,
            text: this.state.text,
            timeStamp: (new Date()).getTime()
        }

        const requestOptions = {
            method: 'POST',
            headers: { 'content-type': 'application/json' },
            body: JSON.stringify(message)
        };
        const url = process.env.NODE_ENV === 'production' ? `rest/chat/messages/${type}` : `http://localhost:8080/ChatWar/rest/chat/messages/${type}`;

        fetch(url, requestOptions)
            .then((response) => {
                if (!response.ok) {
                    alert("Error")
                }
                else {
                    let messages = [...this.state.loggedInUser.messages, message]
                    let user = this.state.loggedInUser;
                    user.messages = messages;
                    this.setState({
                        loggedInUser: user,
                        text: ""
                    }, this.getConversationMessages);
                }
            })
            .catch((error) => {
                console.log(error);
            });
    }

    changeToAll = () => {
        this.setState({
            selectedUser: { username: "ALL" },
            text: ""
        }, this.getConversationMessages);
    }

    render() {
        return (
            <Router basename="/">
                <div className="App">
                    <Route exact path="/" render={props => (
                        <React.Fragment>
                            <Login setLoggedInUser={this.setLoggedInUser}></Login>
                        </React.Fragment>
                    )} />
                    <Route exact path="/login" render={props => (
                        <React.Fragment>
                            <Login setLoggedInUser={this.setLoggedInUser}></Login>
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
                                    {
                                        this.state.loggedInUser !== null ?
                                            <div className="buttons-div">
                                                <button className="users-button" onClick={this.getOnlineUsers}>Online</button>
                                                <button className="users-button" style={{ marginLeft: "20px" }} onClick={this.getAllUsers}>All</button>
                                            </div>
                                            : null
                                    }
                                </div>
                                <div id="conversation-list">
                                    <Conversations users={this.state.users} selectConversation={this.selectConversation} selectedUser={this.state.selectedUser}></Conversations>
                                </div>
                                <div id="lower-left-container">
                                    {
                                        this.state.loggedInUser === null ?
                                            <div className="buttons-div">
                                                <Link className="button-link" to='/login'>Login</Link>
                                                <Link className="button-link" style={{ marginLeft: "20px" }} to='/register'>Sign up</Link>

                                            </div>
                                            :
                                            <div className="buttons-div">
                                                <button className="users-button" onClick={this.logout}>Logout</button>
                                                {
                                                    this.state.selectedUser?.username !== "ALL" ?
                                                        <button className="users-button" style={{ marginLeft: "20px", width: "120px" }} onClick={this.changeToAll}>Send to all</button>
                                                        :
                                                        null
                                                }

                                            </div>
                                    }

                                </div>
                                <div id="chat-title">
                                    <span>{this.state.selectedUser?.username}</span>
                                </div>
                                <div id="chat-message-list">
                                    {this.state.loggedInUser != null && this.state.selectedUser ?
                                        <Conversation messages={this.state.visibleMessages} user={this.state.selectedUser} loggedInUser={this.state.loggedInUser}></Conversation> : null}
                                </div>
                                <form id="chat-form" onSubmit={this.onSendMessage}>
                                    {/* <form > */}
                                    <div>
                                        <input type="text" value={this.state.text} placeholder="type a message" onChange={this.onMessageChange} style={{ width: '80%' }} />
                                        <input type="submit" value="Send"></input>
                                    </div>

                                    {/* </form> */}
                                </form>

                            </div>
                        </React.Fragment>
                    )} />
                </div>
                <Persist
                    name="main-component"
                    data={this.state}
                    debounce={500}
                    onMount={data => this.setState(data)}
                />
            </Router>

        );
    }
}

export default App;
