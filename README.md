# Members

This project allows member management.

## Port

9090

### Mutations

#### Create new Member
```
mutation NewMember{
  createMember(member:
    {
      username: "koch.dejager"
      name: "Koch"
      surname: "de Jager"
      email: "koch.dejager@gmail.com"
      birthdate: "1974-03-05"
      gender: male
    }
  ){
    id
    username
    name
    surname
    email
    birthdate
    gender
    createdAt    
  }
}
```

#### Update Member

```
mutation UpdateMember{
  updateMember(member:
    {
      id: "ebcc362e-487e-4273-b504-4500e4f51421"
      username: "koch.dejager"
      name: "Koch"
      surname: "de Jager"
      email: "koch@gmail.com"
      birthdate: "1974-03-05"
      gender: male
    }
  ){
    id
    username
    name
    surname
    email
    birthdate
    gender
    createdAt    
  }
}
```

#### Enable/Disable Member

```
mutation disableMember {
  disableMember(memberId:"ebcc362e-487e-4273-b504-4500e4f51421"){
    id
    name
    enabled
  }
}

mutation enableMember {
  enableMember(memberId:"ebcc362e-487e-4273-b504-4500e4f51421"){
    id
    name
    enabled
  }
}
```

#### Add/Remove Membership types

```
mutation addMembershipType {
  addMembershipType(memberId:"ebcc362e-487e-4273-b504-4500e4f51421",groupId:"09b3bf8f-284c-4b9c-ac06-39ed4c79361b"){
    id
    name
    enabled
    membershipTypes{
      id
      name
      description
    }
  }
}

mutation removeMembershipType {
  removeMembershipType(memberId:"ebcc362e-487e-4273-b504-4500e4f51421",groupId:"32821dba-c8aa-474d-ade2-4ca5e39a20bd"){
    id
    name
    enabled
    membershipTypes{
      id
      name
      description
    }
  }
}
```

# TOD0

- Get built in email to work so that we can use email verified
- And set password
