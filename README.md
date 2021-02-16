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

#### Enable/Disable Member

```
mutation disableMember {
  disableMember(id:"ebcc362e-487e-4273-b504-4500e4f51421"){
    id
    name
    enabled
  }
}

mutation enableMember {
  enableMember(id:"ebcc362e-487e-4273-b504-4500e4f51421"){
    id
    name
    enabled
  }
}
```

# TOD0

- Get built in email to work so that we can use email verified
- And set password
