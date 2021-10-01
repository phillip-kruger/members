import {graphQLRequest} from '/helper/graphql.js';
import { LitElement, html } from 'https://unpkg.com/lit-element/lit-element.js?module';

const request = `query Member($club: String!, $member: String!) {
                    member(club:$club, id:$member)){
                        name
                        surname
                        email
                        birthdate
                    }
                }`;

class MembersSingle extends LitElement {
    
    static get properties() {
        return {
            club: {type: String, reflect: true},
            member: {type: String, reflect: true},
            name: {type: Boolean, reflect: true},
            data: { type: Object, attribute: false},
            errors: { type: Array, attribute: false }
        };
    }
    
    constructor(){
        super();
        this.club = "";
        this.member = "";
        this.loadAsync();
    }
    
    loadAsync() {
        var variables = {"club": this.club, "member": this.member};
        graphQLRequest(request, variables, "Member").then(response => {        
            this.errors = response.errors;
            this.data = response.data;
        });
    }
    
    render(){
        
        console.log(this.data);
        
        return html`ABCD ${this.club} ${this.member} ${this.name}`
        
    }
}
customElements.define('members-single',MembersSingle);

