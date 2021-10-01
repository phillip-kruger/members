import {graphQLRequest} from '/helper/graphql.js';

import {LitElement, html} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class SimpleGreeting extends LitElement {
    
    static properties = {
        club: {type: String},
        members: {type: String},
    };

    constructor(){
        super();
        this.members = "name surname email"; // default
    }

    render() {
        var variables = {"club": this.club};
        
        var request =   `query MembersAll($club: String!) {
                            members(club:$club){` 
                                + this.fields + `
                            }
                        }`;
        
        var content = graphQLRequest(request, variables, "MembersAll").then(response => {
            var membersResponse = response.data.members;
            var errors = response.errors;
            
            if(membersResponse === null && errors !== null){
                return "There are errors";
            }else{
                var membersFields = this.members.split(" ");
                return html`
                <div class="members">
                    ${membersResponse.map(memberResponse => html`
                        ${membersFields.map(field => html`
                            <div class="member id">
                                ${memberResponse[field]}
                            </div>
                        `)}
                    `)}
                </div>`;
            }
        });
        
        return html`${until(content, html`<loading>Loading...</loading>`)}`;
        
    };
};

customElements.define('simple-greeting', SimpleGreeting);