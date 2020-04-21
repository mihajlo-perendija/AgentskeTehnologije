import React, { Component } from 'react';
import { Link } from 'react-router-dom'
import './Login.css';

class Login extends Component {
    state = {
        username: "",
        password: "",
        submitted: false,
        usernameAlertHidden: true,
        passwordAlertHidden: true,
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
        }
    }

    usernameValid() {
        return this.state.username.length > 3? true : false;
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
                        <span className="login_span" style={{visibility: this.state.usernameAlertHidden ? 'hidden' : 'visible' }} 
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
                        <span className="login_span" style={{visibility: this.state.passwordAlertHidden ? 'hidden' : 'visible' }} 
                        >Invalid password</span>
                    </p>
                    <p className="login_p">
                        <input className="login_input" type="submit" value="Sign In" id="submit"  />
                    </p>
                    <div id="route_to_register_div" >
                        <h2>Don't have an account? <Link to="/register">Register</Link>  </h2>
                        {/* <a href="./register">Register</a> */}
                    </div>
                </form>
            </div>
        );
    }
}

export default Login;