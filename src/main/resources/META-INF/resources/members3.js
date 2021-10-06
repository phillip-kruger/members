import {graphQLRequest} from '/graphql.js';

import {LitElement, html} from 'https://unpkg.com/lit@2.0.0/index.js?module';
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
        
        const variables = {"club": this.club};
        
        const innerQueries = new Map();

        let nodes = [...this.children].map(n => {
            innerQueries.set(camelCase(n.nodeName.toLowerCase()), n.getAttribute("fields"));
            return camelCase(n.nodeName.toLowerCase()) + "{" + n.getAttribute("fields") + "}";
        }).join(' ');
        
        const request =   `query MembersAll($club: String!) {
                            members(club:$club){` 
                                + this.fields + ` ` + nodes + 
                            `}
                        }`;
          
        const content = graphQLRequest(request, variables, "MembersAll").then(response => {
            const membersResponse = response.data.members;
            const errors = response.errors;
            
            if(membersResponse === null && errors !== null){
                return "There are errors";
            }else{
                
                const membersFields = this.fields.split(" ");
                return html`
                
                    <table part="table">
                        <tbody part="tbody">
                            ${membersResponse.map(memberResponse => html`
                            <tr part="tr">
                                ${membersFields.map(field => html`
                                    <td part="td">
                                        ${memberResponse[field]}
                                    </td>
                                `)}
                                
                                ${[...innerQueries.keys()].map(key => html`
                                    ${innerQueries.get(key).split(" ").map(innerField => html`
                                        <td part="td">
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

// From a-b to aB
function camelCase(input) { 
    return input.toLowerCase().replace(/-(.)/g, function(match, group1) {
        return group1.toUpperCase();
    });
}

customElements.define('member-table', MemberTable);

