import React, { Component } from 'react';
import './Conversations.css';
import PropTypes from 'prop-types'

class Conversations extends Component {

    getConversationClass = (user) => {
        //if (this.props.selectedUser === "ALL"){
        //    return "conversation";
        //} else {
            return user.username === this.props.selectedUser?.username ? "conversation active" : "conversation";
        //}
    }

    render() {
        if (this.props.users.length === 0){
            return (null);
        }
        return this.props.users.map((user) => (
            <div className={this.getConversationClass(user)} key={user.id} onClick={this.props.selectConversation.bind(this, user)}>
                <div className="title-text">
                    {user.username}
                </div>
                <div className="created-date">
                    {/* {user.messages ? user.messages[user.messages.length - 1].date.toLocaleString() : ""} */}
                </div>
                <div className="conversation-message">
                    {/* {user.messages ? user.messages[user.messages.length - 1].text : "No messages"} */}
                </div>
            </div>
        )
        );
    }
}

Conversations.propTypes = {
    users: PropTypes.array.isRequired,
    selectedUser: PropTypes.object,
}

export default Conversations;