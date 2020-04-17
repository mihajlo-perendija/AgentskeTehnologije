import React, { Component } from 'react';
import './Conversation.css';
import PropTypes from 'prop-types'

class Conversation extends Component {
    getMessageClass = (message) => {
        return message.sender === this.props.loggedInUser.username ? "message-row my-message": "message-row other-message";
    }

    render() {
        return this.props.user.messages.slice(0).reverse().map((message) => (
            <div className={this.getMessageClass(message)} key={message.date.getTime()}>
                <div className="message-text">{message.text}</div>
                <div className="message-time">{message.date.toLocaleString()}</div>
            </div>
        )
        );
    }
}

Conversation.propTypes = {
    user: PropTypes.object.isRequired,
    loggedInUser: PropTypes.object.isRequired
}


export default Conversation;