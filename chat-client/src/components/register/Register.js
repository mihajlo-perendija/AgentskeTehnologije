import React, { Component } from 'react';
import './Register.css';

class Register extends Component {
    state = {
        username: "",
        password: "",
        confirmPassword: "",
        submitted: false,
        usernameAlertHidden: true,
        passwordAlertHidden: true,
        confirmPasswordAlertHidden: true
    }

    onChange = (e) => {
        this.setState({ [e.target.name]: e.target.value }, this.validateInput);
    }

    onSubmit = (e) => {
        e.preventDefault();
        this.setState({ submitted: true }, this.validateInput);
    }

    validateInput() {
        if (this.state.submitted) {
            this.usernameValid() ? this.setState({ usernameAlertHidden: true }) : this.setState({ usernameAlertHidden: false });
            this.passwordValid() ? this.setState({ passwordAlertHidden: true }) : this.setState({ passwordAlertHidden: false });
            this.confirmPasswordValid() ? this.setState({ confirmPasswordAlertHidden: true }) : this.setState({ confirmPasswordAlertHidden: false });
        }
    }

    usernameValid() {
        return this.state.username.length > 3? true : false;
    }

    passwordValid() {
        return this.state.password.length > 5 ? true : false;
    }

    confirmPasswordValid() {
        return this.state.password === this.state.confirmPassword;
    }

    render() {
        return (
            <div >
                <form onSubmit={this.onSubmit}>
                    <h2>Sign Up</h2>
                    <p>
                        <input
                            type="text"
                            name="username"
                            placeholder="Username"
                            value={this.state.username}
                            onChange={this.onChange}
                        />
                        <span style={{visibility: this.state.usernameAlertHidden ? 'hidden' : 'visible' }} 
                        >Enter a username longer than 4 characters</span>
                    </p>
                    <p>
                        <input
                            type="password"
                            name="password"
                            placeholder="Password"
                            value={this.state.password}

                            onChange={this.onChange}
                        />
                        <span style={{visibility: this.state.passwordAlertHidden ? 'hidden' : 'visible' }} 
                        >Enter a password longer than 5 characters</span>
                    </p>
                    <p>
                        <input
                            type="password"
                            name="confirmPassword"
                            placeholder="Confirm password"
                            value={this.state.confirmPassword}

                            onChange={this.onChange}
                        />
                        <span style={{visibility: this.state.confirmPasswordAlertHidden ? 'hidden' : 'visible' }} 
                        >Your passwords do not match</span>
                    </p>
                    <p>
                        <input type="submit" value="Create My Account" id="submit"  />
                    </p>
                </form>
            </div>
        );
    }
}

export default Register;