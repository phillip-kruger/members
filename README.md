# Members

This project allows member management.

## Port

9090

### Mutations

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

# TOD0

- Get built in email to work so that we can use email verified
- And set password
