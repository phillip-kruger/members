import {graphQLRequest} from '/graphql.js';

import {LitElement, html, css} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class MemberTable extends LitElement {
    
    static properties = {
        club: {type: String},
        fields: {type: String},
    };

    constructor(){
        super();
        this.fields = "name surname email"; // default
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
                
                var membersFields = this.fields.split(" ");
                return html`
                
                    <table part="members-table">
                        <thead>
                            <tr>
                                ${membersFields.map(field => html`
                                    <th>
                                        ${camelize(field)}
                                    </th>
                                `)}
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            ${membersResponse.map(memberResponse => html`
                            <tr>
                                ${membersFields.map(field => html`
                                    <td>
                                        ${memberResponse[field]}
                                    </td>
                                `)}
                            </tr>`)}
                        </tbody>
                    </table>`;
            }
        });
        
        return html`${until(content, html`<loading>Loading...</loading>`)}`;
        
    };
};

function camelize(str) {
    var f = str.charAt(0);
    return f.toUpperCase() + str.slice(1);
}

customElements.define('member-table', MemberTable);