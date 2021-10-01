import {graphQLRequest} from '/helper/graphql.js';

import {LitElement, html} from 'https://unpkg.com/lit@2.0.0/index.js?module';
import {until} from 'https://unpkg.com/lit@2.0.0/directives/until.js?module';

export class MembersNesting extends LitElement {
    
    static properties = {
        club: {type: String},
    };

    render() {
        
        var attributes = this.attributes;
        console.log(attributes.length);
        
        return html`<div class="members">
                        <slot></slot>
                    </div>`;
        
    };
};

customElements.define('members-nesting', MembersNesting);

class MemberId extends LitElement {
    render() {
        return html`<div class="member id">id</div>`;
    }
}

customElements.define('member-id', MemberId);

class MemberName extends LitElement {
    render() {
        return html`<div class="member name">name</div>`;
    }
}

customElements.define('member-name', MemberName);

class MemberSurname extends LitElement {
    render() {
        return html`<div class="member surname">surname</div>`;
    }
}

customElements.define('member-surname', MemberSurname);

class MemberEmail extends LitElement {
    render() {
        
        
        var parent = this.getParent();
        
        var attributes = parent.attributes;
        console.log("parent = " + attributes.length);
        
        
        return html`<div class="member email">email</div>`;
    }
}

customElements.define('member-email', MemberEmail);


//class MemberField extends LitElement {
//    
//    static properties = {
//        field: {type: String}, // attribute: false},
//    };
//    
//    constructor(){
//        super();
//    }
//    
//    render() {
//        return html`<div class="member ${field}">${field}</div>`;
//    }
//}
//
//const field1 = MemberField;
//field1.field = "field1";
//const field2 = MemberField;
//field2.field = "field2";
//
//customElements.define('member-field1', field1);
//customElements.define('member-field2', field2);
