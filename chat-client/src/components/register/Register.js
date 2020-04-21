import React, { Component } from 'react';
import {Redirect} from 'react-router-dom';
import './Register.css';

class Register extends Component {
    constructor(props) {
        super(props)
        this.state = {
            username: "",
            password: "",
            confirmPassword: "",
            submitted: false,
            usernameAlertHidden: true,
            passwordAlertHidden: true,
            confirmPasswordAlertHidden: true,
            registered: false
        }
    }


    onChange = (e) => {
        this.setState({ [e.target.name]: e.target.value }, this.validateInput);
    }

    onSubmit = (e) => {
        e.preventDefault();
        this.setState({ submitted: true }, this.sendRegisterRequest);
    }

    sendRegisterRequest = () => {
        this.validateInput(() => {
            if (this.state.usernameAlertHidden && this.state.passwordAlertHidden && this.state.confirmPasswordAlertHidden) {

                const requestOptions = {
                    method: 'POST',
                    headers: { 'content-type': 'application/json' },
                    body: JSON.stringify({
                        username: this.state.username,
                        password: this.state.password
                    })
                };
                const url = process.env.NODE_ENV === 'production' ? "rest/chat/users/register" : "http://localhost:8080/ChatWar/rest/chat/users/register";

                fetch(url, requestOptions)
                    .then((response) => {
                        if (!response.ok) {
                            alert("Invalid username or password")
                        }
                        else {
                            alert("Successfuly registered");
                            this.setState({registered: true});
                        }
                    })
                    .catch((error) => {
                        console.log(error);
                    });
            }
        });
    }

    validateInput(proceed) {
        if (this.state.submitted) {
            this.setState({
                usernameAlertHidden: this.usernameValid(),
                passwordAlertHidden: this.passwordValid(),
                confirmPasswordAlertHidden: this.confirmPasswordValid()
            }, proceed);
        }
    }

    usernameValid() {
        return this.state.username.length > 3 ? true : false;
    }

    passwordValid() {
        return this.state.password.length > 5 ? true : false;
    }

    confirmPasswordValid() {
        return this.state.password === this.state.confirmPassword;
    }

    render() {
        if (this.state.registered === true) {
            return <Redirect to='/login' />
          }

        return (
            <div >
                <form onSubmit={this.onSubmit} id="register_form">
                    <h2 id="register_h2">Sign Up</h2>
                    <p className="register_p">
                        <input
                            className="register_input"
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={this.state.username}
                            onChange={this.onChange}
                        />
                        <span className="register_span" style={{ visibility: this.state.usernameAlertHidden ? 'hidden' : 'visible' }}
                        >Enter a username longer than 4 characters</span>
                    </p>
                    <p className="register_p">
                        <input
                            className="register_input"
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={this.state.password}

                            onChange={this.onChange}
                        />
                        <span className="register_span" style={{ visibility: this.state.passwordAlertHidden ? 'hidden' : 'visible' }}
                        >Enter a password longer than 5 characters</span>
                    </p>
                    <p className="register_p">
                        <input
                            className="register_input"
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm password"
                            value={this.state.confirmPassword}

                            onChange={this.onChange}
                        />
                        <span className="register_span" style={{ visibility: this.state.confirmPasswordAlertHidden ? 'hidden' : 'visible' }}
                        >Your passwords do not match</span>
                    </p>
                    <p className="register_p">
                        <input className="register_input" type="submit" value="Create My Account" id="submit" />
                    </p>
                </form>
            </div>
        );
    }
}

export default Register;