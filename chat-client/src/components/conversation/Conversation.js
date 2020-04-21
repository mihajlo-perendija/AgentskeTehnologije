import React, { Component } from 'react';
import './Conversation.css';
import PropTypes from 'prop-types'

class Conversation extends Component {
    getMessageClass = (message) => {
        return message.sender === this.props.loggedInUser.username ? "message-row my-message" : "message-row other-message";
    }

    render() {
        if (!this.props.messages) {
            return (null);
        }
        return this.props.messages.slice(0).reverse().map((message) => (
            <div className={this.getMessageClass(message)} key={(new Date(message.timeStamp)).getTime()}>
                <div className="message-text">{message.text}</div>
                <div>
                    <span className="message-username">{message.sender + " at "}</span>
                    <span className="message-time">{(new Date(message.timeStamp)).toLocaleString()}</span>
                </div>
            </div>
        )
        );
    }
}

Conversation.propTypes = {
    user: PropTypes.object.isRequired,
    loggedInUser: PropTypes.object.isRequired,
    messages: PropTypes.array.isRequired
}


export default Conversation;