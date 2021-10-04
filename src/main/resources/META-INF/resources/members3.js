import {graphQLRequest} from '/helper/graphql.js';

import {LitElement, html} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class MemberList extends LitElement {
    
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
        
        const innerQueries = new Map();

        let nodes = [...this.children].map(n => {
            innerQueries.set(camelCase(n.nodeName.toLowerCase()), n.getAttribute("fields"));
            return camelCase(n.nodeName.toLowerCase()) + "{" + n.getAttribute("fields") + "}";
        }).join(' ');
        
        var request =   `query MembersAll($club: String!) {
                            members(club:$club){` 
                                + this.fields + ` ` + nodes + 
                            `}
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
                                ${[...innerQueries.keys()].map(key => html`
                                    
                                    ${innerQueries.get(key).split(" ").map(innerField => html`
                                    <th>
                                        ${camelize(key) + " " + camelize(innerField)}
                                    </th>    
                                    `)}
                                `)}
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
                                
                                ${[...innerQueries.keys()].map(key => html`
                                    ${innerQueries.get(key).split(" ").map(innerField => html`
                                        <td>
                                        ${memberResponse[key].map(value => html`
                                            ${value[innerField]}
                                        `)}
                                        </td>
                                    `)}
                                `)}
                            </tr>`)}
                        </tbody>
                    </table>`;
            }
        });
        
        return html`${until(content, html`<loading>Loading...</loading>`)}`;
        
    };
    
};


// From aB to A b
function camelize(str) {
    var f = str.charAt(0);
    return f.toUpperCase() + str.slice(1);
}

// From a-b to aB
function camelCase(input) { 
    return input.toLowerCase().replace(/-(.)/g, function(match, group1) {
        return group1.toUpperCase();
    });
}

customElements.define('member-list', MemberList);

