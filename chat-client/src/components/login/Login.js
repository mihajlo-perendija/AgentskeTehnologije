import React, { Component } from 'react';
import { Link, withRouter } from 'react-router-dom'
import './Login.css';
import PropTypes from 'prop-types'

class Login extends Component {
    constructor(props) {
        super(props);
        this.state = {
            username: "",
            password: "",
            submitted: false,
            usernameAlertHidden: true,
            passwordAlertHidden: true,
        };
    }

    onChange = (e) => {
        this.setState({ [e.target.name]: e.target.value }, this.validateInput);
    }

    onSubmit = (e) => {
        e.preventDefault();
        this.setState({ submitted: true }, this.sendLoginRequest);
    }

    sendLoginRequest = () => {
        this.validateInput(() => {
            if (this.state.usernameAlertHidden && this.state.passwordAlertHidden) {

                const requestOptions = {
                    method: 'POST',
                    headers: { 'content-type': 'application/json' },
                    body: JSON.stringify({
                        username: this.state.username,
                        password: this.state.password
                    })
                };
                const url = process.env.NODE_ENV === 'production' ? "rest/chat/users/login" : "http://localhost:8080/ChatWar/rest/chat/users/login";

                fetch(url, requestOptions)
                    .then((response) => {
                        if (!response.ok) {
                            alert("Invalid username or password")
                        }
                        else {
                            alert("Successfuly logged in");
                            return response.json();
                        }
                    })
                    .then((data) => {
                        if (data?.username) {
                            this.props.setLoggedInUser(data);
                            this.props.history.push('/chat');
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        });
    }

    validateInput(proceedCallback) {
        if (this.state.submitted) {
            this.setState({
                usernameAlertHidden: this.usernameValid(),
                passwordAlertHidden: this.passwordValid(),
            }, proceedCallback);
        }
    }

    usernameValid() {
        return this.state.username.length > 3 ? true : false;
    }

    passwordValid() {
        return this.state.password.length > 5 ? true : false;
    }

    render() {
        return (
            <div >
                <form onSubmit={this.onSubmit} id="login_form">
                    <h2 id="login_h2">Sign In</h2>
                    <p className="login_p">
                        <input
                            className="login_input"
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={this.state.username}
                            onChange={this.onChange}
                        />
                        <span className="login_span" style={{ visibility: this.state.usernameAlertHidden ? 'hidden' : 'visible' }}
                        >Invalid username</span>
                    </p>
                    <p className="login_p">
                        <input
                            className="login_input"
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={this.state.password}

                            onChange={this.onChange}
                        />
                        <span className="login_span" style={{ visibility: this.state.passwordAlertHidden ? 'hidden' : 'visible' }}
                        >Invalid password</span>
                    </p>
                    <p className="login_p">
                        <input className="login_input" type="submit" value="Sign In" id="submit" />
                    </p>
                    <div id="route_to_register_div" >
                        <h2>Don't have an account? <Link to="/register">Register</Link>  </h2>
                    </div>
                </form>
            </div>
        );
    }
}

Login.propTypes = {
    setLoggedInUser: PropTypes.func.isRequired,
    match: PropTypes.object.isRequired,
    location: PropTypes.object.isRequired,
    history: PropTypes.object.isRequired
}

export default withRouter(Login);