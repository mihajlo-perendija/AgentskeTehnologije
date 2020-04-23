(this["webpackJsonpchat-client"]=this["webpackJsonpchat-client"]||[]).push([[0],{25:function(e,t,n){e.exports=n(39)},30:function(e,t,n){},31:function(e,t,n){},36:function(e,t,n){},37:function(e,t,n){},38:function(e,t,n){},39:function(e,t,n){"use strict";n.r(t);var a=n(0),s=n.n(a),r=n(20),i=n.n(r),l=n(24),o=n(4),c=n(5),u=n(7),d=n(6),m=n(10),g=n(9),p=n(21),h=(n(30),n(13)),f=(n(31),function(e){Object(u.a)(n,e);var t=Object(d.a)(n);function n(e){var a;return Object(o.a)(this,n),(a=t.call(this,e)).onChange=function(e){a.setState(Object(h.a)({},e.target.name,e.target.value),a.validateInput)},a.onSubmit=function(e){e.preventDefault(),a.setState({submitted:!0},a.sendRegisterRequest)},a.sendRegisterRequest=function(){a.validateInput((function(){if(a.state.usernameAlertHidden&&a.state.passwordAlertHidden&&a.state.confirmPasswordAlertHidden){var e={method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({username:a.state.username,password:a.state.password})};fetch("rest/chat/users/register",e).then((function(e){e.ok?(alert("Successfuly registered"),a.setState({registered:!0})):alert("Username already exists")})).catch((function(e){console.log(e)}))}}))},a.state={username:"",password:"",confirmPassword:"",submitted:!1,usernameAlertHidden:!0,passwordAlertHidden:!0,confirmPasswordAlertHidden:!0,registered:!1},a}return Object(c.a)(n,[{key:"validateInput",value:function(e){this.state.submitted&&this.setState({usernameAlertHidden:this.usernameValid(),passwordAlertHidden:this.passwordValid(),confirmPasswordAlertHidden:this.confirmPasswordValid()},e)}},{key:"usernameValid",value:function(){return this.state.username.length>3}},{key:"passwordValid",value:function(){return this.state.password.length>5}},{key:"confirmPasswordValid",value:function(){return this.state.password===this.state.confirmPassword}},{key:"render",value:function(){return!0===this.state.registered?s.a.createElement(g.a,{to:"/login"}):s.a.createElement("div",null,s.a.createElement("form",{onSubmit:this.onSubmit,id:"register_form"},s.a.createElement("h2",{id:"register_h2"},"Sign Up"),s.a.createElement("p",{className:"register_p"},s.a.createElement("input",{className:"register_input",type:"text",name:"username",placeholder:"Username",value:this.state.username,onChange:this.onChange}),s.a.createElement("span",{className:"register_span",style:{visibility:this.state.usernameAlertHidden?"hidden":"visible"}},"Enter a username longer than 4 characters")),s.a.createElement("p",{className:"register_p"},s.a.createElement("input",{className:"register_input",type:"password",name:"password",placeholder:"Password",value:this.state.password,onChange:this.onChange}),s.a.createElement("span",{className:"register_span",style:{visibility:this.state.passwordAlertHidden?"hidden":"visible"}},"Enter a password longer than 5 characters")),s.a.createElement("p",{className:"register_p"},s.a.createElement("input",{className:"register_input",type:"password",name:"confirmPassword",placeholder:"Confirm password",value:this.state.confirmPassword,onChange:this.onChange}),s.a.createElement("span",{className:"register_span",style:{visibility:this.state.confirmPasswordAlertHidden?"hidden":"visible"}},"Your passwords do not match")),s.a.createElement("p",{className:"register_p"},s.a.createElement("input",{className:"register_input",type:"submit",value:"Create My Account",id:"submit"}))))}}]),n}(a.Component)),v=(n(36),function(e){Object(u.a)(n,e);var t=Object(d.a)(n);function n(e){var a;return Object(o.a)(this,n),(a=t.call(this,e)).onChange=function(e){a.setState(Object(h.a)({},e.target.name,e.target.value),a.validateInput)},a.onSubmit=function(e){e.preventDefault(),a.setState({submitted:!0},a.sendLoginRequest)},a.sendLoginRequest=function(){a.validateInput((function(){if(a.state.usernameAlertHidden&&a.state.passwordAlertHidden){var e={method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify({username:a.state.username,password:a.state.password})};fetch("rest/chat/users/login",e).then((function(e){if(e.ok)return alert("Successfuly logged in"),e.json();alert("Invalid username or password")})).then((function(e){(null===e||void 0===e?void 0:e.username)&&(a.props.setLoggedInUser(e),a.props.history.push("/chat"))})).catch((function(e){console.log(e)}))}}))},a.state={username:"",password:"",submitted:!1,usernameAlertHidden:!0,passwordAlertHidden:!0},a}return Object(c.a)(n,[{key:"validateInput",value:function(e){this.state.submitted&&this.setState({usernameAlertHidden:this.usernameValid(),passwordAlertHidden:this.passwordValid()},e)}},{key:"usernameValid",value:function(){return this.state.username.length>3}},{key:"passwordValid",value:function(){return this.state.password.length>5}},{key:"render",value:function(){return s.a.createElement("div",null,s.a.createElement("form",{onSubmit:this.onSubmit,id:"login_form"},s.a.createElement("h2",{id:"login_h2"},"Sign In"),s.a.createElement("p",{className:"login_p"},s.a.createElement("input",{className:"login_input",type:"text",name:"username",placeholder:"Username",value:this.state.username,onChange:this.onChange}),s.a.createElement("span",{className:"login_span",style:{visibility:this.state.usernameAlertHidden?"hidden":"visible"}},"Invalid username")),s.a.createElement("p",{className:"login_p"},s.a.createElement("input",{className:"login_input",type:"password",name:"password",placeholder:"Password",value:this.state.password,onChange:this.onChange}),s.a.createElement("span",{className:"login_span",style:{visibility:this.state.passwordAlertHidden?"hidden":"visible"}},"Invalid password")),s.a.createElement("p",{className:"login_p"},s.a.createElement("input",{className:"login_input",type:"submit",value:"Sign In",id:"submit"})),s.a.createElement("div",{id:"route_to_register_div"},s.a.createElement("h2",null,"Don't have an account? ",s.a.createElement(m.b,{to:"/register"},"Register"),"  "))))}}]),n}(a.Component)),b=Object(g.f)(v),E=(n(37),function(e){Object(u.a)(n,e);var t=Object(d.a)(n);function n(){var e;Object(o.a)(this,n);for(var a=arguments.length,s=new Array(a),r=0;r<a;r++)s[r]=arguments[r];return(e=t.call.apply(t,[this].concat(s))).getConversationClass=function(t){var n;return t.username===(null===(n=e.props.selectedUser)||void 0===n?void 0:n.username)?"conversation active":"conversation"},e}return Object(c.a)(n,[{key:"render",value:function(){var e=this;return 0===this.props.users.length?null:this.props.users.map((function(t){return s.a.createElement("div",{className:e.getConversationClass(t),key:t.id,onClick:e.props.selectConversation.bind(e,t)},s.a.createElement("div",{className:"title-text"},t.username),s.a.createElement("div",{className:"created-date"}),s.a.createElement("div",{className:"conversation-message"}))}))}}]),n}(a.Component)),w=(n(38),function(e){Object(u.a)(n,e);var t=Object(d.a)(n);function n(){var e;Object(o.a)(this,n);for(var a=arguments.length,s=new Array(a),r=0;r<a;r++)s[r]=arguments[r];return(e=t.call.apply(t,[this].concat(s))).getMessageClass=function(t){return t.sender===e.props.loggedInUser.username?"message-row my-message":"message-row other-message"},e}return Object(c.a)(n,[{key:"render",value:function(){var e=this;return this.props.messages?this.props.messages.slice(0).reverse().map((function(t){return s.a.createElement("div",{className:e.getMessageClass(t),key:new Date(t.timeStamp).getTime()},s.a.createElement("div",{className:"message-text"},t.text),s.a.createElement("div",null,s.a.createElement("span",{className:"message-username"},t.sender+" at "),s.a.createElement("span",{className:"message-time"},new Date(t.timeStamp).toLocaleString())))})):null}}]),n}(a.Component)),y="ws://localhost:8080/ChatWar/ws/",U=function(e){Object(u.a)(n,e);var t=Object(d.a)(n);function n(){var e;Object(o.a)(this,n);for(var a=arguments.length,s=new Array(a),r=0;r<a;r++)s[r]=arguments[r];return(e=t.call.apply(t,[this].concat(s))).state={loggedInUser:null,users:[],selectedUser:{},text:"",visibleMessages:[]},e.selectConversation=function(t){e.setState({selectedUser:t,text:""},e.getConversationMessages)},e.getConversationMessages=function(){var t;t="ALL"===e.state.selectedUser.username?e.state.loggedInUser.messages.filter((function(e){return"ALL"===e.receiver})):e.state.loggedInUser.messages.filter((function(t){return t.sender===e.state.selectedUser.username&&t.receiver===e.state.loggedInUser.username||t.sender===e.state.loggedInUser.username&&t.receiver===e.state.selectedUser.username})),e.setState({visibleMessages:t})},e.getUserMessages=function(){var t="rest/chat/messages/".concat(e.state.loggedInUser.username);fetch(t,{method:"GET"}).then((function(e){if(e.ok)return e.json();alert("Error")})).then((function(t){t||(t=[]);var n=e.state.loggedInUser;n.messages=t,e.setState({loggedInUser:n,text:""})})).catch((function(e){console.log(e)}))},e.getAllDataOnLogin=function(){e.getUserMessages(),e.getOnlineUsers(),e.websocket=new WebSocket(y+e.state.loggedInUser.username),e.websocket.onopen=function(){console.log("connected")},e.websocket.onmessage=function(t){var n=JSON.parse(t.data),a=[].concat(Object(l.a)(e.state.loggedInUser.messages),[n]),s=e.state.loggedInUser;s.messages=a,e.setState({loggedInUser:s,text:""},e.getConversationMessages)},e.websocket.onclose=function(){console.log("disconnected"),e.setState({websocket:new WebSocket(y+e.state.loggedInUser.username)})}},e.setLoggedInUser=function(t){e.setState({loggedInUser:t},(function(){e.getAllDataOnLogin()}))},e.logout=function(){var t="rest/chat/users/loggedIn/".concat(e.state.loggedInUser.username);fetch(t,{method:"DELETE",headers:{"content-type":"application/json"}}).then((function(t){t.ok?e.setState({loggedInUser:null,selectedUser:null,users:[]}):alert("Error")})).catch((function(e){console.log(e)}))},e.getOnlineUsers=function(){fetch("rest/chat/users/loggedIn").then((function(e){if(e.ok)return e.json();alert("Error")})).then((function(t){e.setState({users:t.filter((function(t){return t.username!==e.state.loggedInUser.username}))})})).catch((function(e){console.log(e)}))},e.getAllUsers=function(){fetch("rest/chat/users/registered").then((function(e){if(e.ok)return e.json();alert("Error")})).then((function(t){e.setState({users:t.filter((function(t){return t.username!==e.state.loggedInUser.username}))})})).catch((function(e){console.log(e)}))},e.onMessageChange=function(t){e.setState({text:t.target.value})},e.onSendMessage=function(t){if(t.preventDefault(),e.state.loggedInUser){var n;n="ALL"===e.state.selectedUser.username?"all":"user";var a={sender:e.state.loggedInUser.username,receiver:e.state.selectedUser.username,text:e.state.text,timeStamp:(new Date).getTime()},s={method:"POST",headers:{"content-type":"application/json"},body:JSON.stringify(a)},r="rest/chat/messages/".concat(n);fetch(r,s).then((function(e){e.ok||alert("Error")})).catch((function(e){console.log(e)}))}},e.changeToAll=function(){e.setState({selectedUser:{username:"ALL"},text:""},e.getConversationMessages)},e}return Object(c.a)(n,[{key:"render",value:function(){var e=this;return s.a.createElement(m.a,{basename:"/"},s.a.createElement("div",{className:"App"},s.a.createElement(g.b,{exact:!0,path:"/",render:function(t){return s.a.createElement(s.a.Fragment,null,s.a.createElement(b,{setLoggedInUser:e.setLoggedInUser}))}}),s.a.createElement(g.b,{exact:!0,path:"/login",render:function(t){return s.a.createElement(s.a.Fragment,null,s.a.createElement(b,{setLoggedInUser:e.setLoggedInUser}))}}),s.a.createElement(g.b,{exact:!0,path:"/register",render:function(e){return s.a.createElement(s.a.Fragment,null,s.a.createElement(f,null))}}),s.a.createElement(g.b,{exact:!0,path:"/chat",render:function(t){var n,a;return s.a.createElement(s.a.Fragment,null,s.a.createElement("div",{id:"chat-container"},s.a.createElement("div",{id:"search-container"},null!==e.state.loggedInUser?s.a.createElement("div",{className:"buttons-div"},s.a.createElement("button",{className:"users-button",onClick:e.getOnlineUsers},"Online"),s.a.createElement("button",{className:"users-button",style:{marginLeft:"20px"},onClick:e.getAllUsers},"All")):null),s.a.createElement("div",{id:"conversation-list"},s.a.createElement(E,{users:e.state.users,selectConversation:e.selectConversation,selectedUser:e.state.selectedUser})),s.a.createElement("div",{id:"lower-left-container"},null===e.state.loggedInUser?s.a.createElement("div",{className:"buttons-div"},s.a.createElement(m.b,{className:"button-link",to:"/login"},"Login"),s.a.createElement(m.b,{className:"button-link",style:{marginLeft:"20px"},to:"/register"},"Sign up")):s.a.createElement("div",{className:"buttons-div"},s.a.createElement("button",{className:"users-button",onClick:e.logout},"Logout"),"ALL"!==(null===(n=e.state.selectedUser)||void 0===n?void 0:n.username)?s.a.createElement("button",{className:"users-button",style:{marginLeft:"20px",width:"120px"},onClick:e.changeToAll},"Send to all"):null)),s.a.createElement("div",{id:"chat-title"},s.a.createElement("span",null,null===(a=e.state.selectedUser)||void 0===a?void 0:a.username)),s.a.createElement("div",{id:"chat-message-list"},null!=e.state.loggedInUser&&e.state.selectedUser?s.a.createElement(w,{messages:e.state.visibleMessages,user:e.state.selectedUser,loggedInUser:e.state.loggedInUser}):null),s.a.createElement("form",{id:"chat-form",onSubmit:e.onSendMessage},s.a.createElement("div",null,s.a.createElement("input",{type:"text",value:e.state.text,placeholder:"type a message",onChange:e.onMessageChange,style:{width:"80%"}}),s.a.createElement("input",{type:"submit",value:"Send"})))))}})),s.a.createElement(p.a,{name:"main-component",data:this.state,debounce:500,onMount:function(t){return e.setState(t)}}))}}]),n}(a.Component);i.a.render(s.a.createElement(s.a.StrictMode,null,s.a.createElement(U,null)),document.getElementById("root"))}},[[25,1,2]]]);
//# sourceMappingURL=main.6becb465.chunk.js.map