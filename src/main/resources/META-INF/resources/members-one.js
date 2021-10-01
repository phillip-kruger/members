import {graphQLRequest} from '/helper/graphql.js';

import {LitElement, html, css} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';
import styles from 'https://cdn.jsdelivr.net/npm/bulma@0.9.3/css/bulma.min.css';

export class MembersOne extends LitElement {
    
    static properties = {
        club: {type: String},
    };

    render() {
        var variables = {"club": this.club};
        
        var request =   `query MembersAll($club: String!) {
                            members(club:$club){
                                name
                                surname
                                email
                            }
                        }`;
        
        var content = graphQLRequest(request, variables, "MembersAll").then(response => {
            var membersResponse = response.data.members;
            var errors = response.errors;
            
            if(membersResponse === null && errors !== null){
                return "There are errors";
            }else{
                
                return html`
                    <table part="members-table">
                        <thead>
                            <tr>
                                <th>Name</th>
                                <th>Surname</th>
                                <th>Email</th>
                                <th></th>
                            </tr>
                        </thead>
                        <tbody>
                            ${membersResponse.map(memberResponse => html`
                            <tr>
                                <td>${memberResponse['name']}</td>
                                <td>${memberResponse['surname']}</td>
                                <td>${memberResponse['email']}</td>
                                <td></td>
                            </tr>
                            `)}
                        </tbody>
                    </table>`;
            }
        });
        
        return html`${until(content, html`<loading>Loading...</loading>`)}`;
        
    };
};

customElements.define('members-one', MembersOne);