/* 
 * Some graphql related helper functions
 */
let documentpath = 'http://localhost:9090/graphql';

export async function graphQLRequest(query, variables = {}, operationName = ""){
    
    try {
        let response = await fetch(documentpath, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(
                {
                    "query":query,
                    "variables":variables,
                    "operationName":operationName
                }
            )
        });
        return await response.json();
    } catch (error) {
        throw error;
    }
}