import React from 'react';
import './App.css';
import { BrowserRouter as Router, Route } from 'react-router-dom'
import Register from './components/register/Register.js'
import Login from './components/login/Login.js'

function App() {
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
            </div>
        </Router>
    );
}

export default App;
