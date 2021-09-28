/* 
 * Some graphql related helper functions
 * @author Phillip Kruger (phillip.kruger@gmail.com)
 * 
 * TODO: Add authentication
 */
const documentpath = '/graphql';

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