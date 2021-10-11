import {graphQLRequest} from '/graphql.js';

import {LitElement, html, css} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class MemberTable extends LitElement {
    
    static properties = {
        club: {type: String},
    };

    render() {
        const variables = {"club": this.club};
        
        const request =   `query MembersAll($club: String!) {
                            members(club:$club){
                                name
                                surname
                                email
                            }
                        }`;
        
        const content = graphQLRequest(request, variables, "MembersAll").then(response => {
            const membersResponse = response.data.members;
            const errors = response.errors;
            
            if(membersResponse === null && errors !== null){
                return "There are errors";
            }else{
                
                return html`
                    <table>
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Surname</th>
                                <th>Email</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${membersResponse.map(memberResponse => html`
                            <tr>
                                <td>${memberResponse['name']}</td>
                                <td>${memberResponse['surname']}</td>
                                <td>${memberResponse['email']}</td>
                            </tr>
                            `)}
                        </tbody>
                    </table>`;
            }
        });
        
        return html`${until(content, html`<loading>Loading...</loading>`)}`;
        
    };
};

customElements.define('member-table', MemberTable);