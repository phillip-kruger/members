import {graphQLRequest} from '/helper/graphql.js';
import { LitElement, html } from 'https://unpkg.com/lit@2.0.0/index.js?module';

const request = `query MembersAll($club: String!) {
                    members(club:$club){
                        id
                        name
                        surname
                        email
                    }
                }`;

export class MembersAll extends LitElement {
    
    static properties = {
        club: { type: String, attribute: "club"},
        name: {},
        data: { type: Object , attribute: false },
        errors: { type: Array, attribute: false },
    };
    
    constructor(){
        super();
        this.loadAsync();
    }
    
//    get club() {
//        return this.getAttribute('club');
//    }
    
    loadAsync() {
        var variables = {"club": this.club};    
        graphQLRequest(request, variables, "MembersAll").then(response => {        
            this.errors = response.errors;
            this.data = response.data;
        });
    }
    
    render(){
        var members = this.data.members;
        if(members === null && this.errors !== null){
            console.log("TODO: Error " + this.errors);
        }else{
            return html`
                <div class="members">
                    ${members.map(member => html`
                        <div class="member id">
                            ${member.id}
                        </div>
                        <div class="member name">
                            ${member.name}
                        </div>
                        <div class="member surname">
                            ${member.surname}
                        </div>
                        <div class="member email">
                            ${member.email}
                        </div>
                    `)}
                </div>`
        }
    }
}
customElements.define('members-all',MembersAll);