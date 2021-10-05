/* 
 * Some graphql related helper functions
 */
var documentpath = '/graphql';

const scriptTags = document.scripts;
for (var i = 0; i < scriptTags.length; i++) {
    let url = new URL(scriptTags[i].src);
    if(url.pathname.includes("/members") && url.pathname.endsWith(".js")){
        documentpath = url.origin + documentpath;
    }
}

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