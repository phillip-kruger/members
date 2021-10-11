import {graphQLRequest} from '/graphql.js';

import {LitElement, html} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class MemberText extends LitElement {
    
    static properties = {
        club: {type: String},
        username: {type: String},
        fields: {type: String},
    };

    constructor(){
        super();
        this.fields = "name surname email"; // default
        this.username = "";
    }

    render() {
        
        const variables = {club: this.club, username: this.username };

        const request =     `query Member($club: String!, $username: String!) {
                                searchMembers(club:$club,username:$username){` 
                                + this.fields + 
                                `}
                            }`;
        
        const content = graphQLRequest(request, variables, "Member").then(response => {
            if(response.data === null){
                return html``;
            }else{
                const membersResponse = response.data.searchMembers;
                const errors = response.errors;

                if(membersResponse === null && errors !== null){
                    return "There are errors";
                }else{

                    const membersFields = this.fields.split(" ");
                    return html`
                            ${membersResponse.map(memberResponse => html`
                                    ${membersFields.map(field => html`
                                            ${memberResponse[field]} 
                                    `)}
                                `)}
                            `;
                }
            }
            });
            return html`${until(content, html`<loading>Loading...</loading>`)}`;
    };
};

customElements.define('member-text', MemberText);

