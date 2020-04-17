import React, { Component } from 'react';
import './Conversations.css';
import PropTypes from 'prop-types'

class Conversations extends Component {

    getConversationClass = (user) => {
        return user.username === this.props.selectedUser.username ? "conversation active": "conversation";
    }

    render() {
        return this.props.users.map((user) => (
            <div className={this.getConversationClass(user)} key={user.id} onClick={this.props.selectConversation.bind(this, user)}>
                <div className="title-text">
                    {user.username}
                </div>
                <div className="created-date">
                    {user.messages[user.messages.length-1].date.toLocaleString()}
                </div>
                <div className="conversation-message">
                {user.messages[user.messages.length-1].text}
                </div>
            </div>
        )
        );
    }
}

Conversations.propTypes = {
    users: PropTypes.array.isRequired,
    selectedUser: PropTypes.object.isRequired,
}

export default Conversations;