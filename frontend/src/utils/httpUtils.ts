/**
 * Synchronous single post request.
 */
export async function post<A extends object>(subDirect: string, body: object) {
  // Initialize
  const bodyJsonified = {
    method: "POST",
    headers: {
      "content-type": "application/json;charset=UTF-8",
    },
    body: JSON.stringify(body),
  };

  // Shot request & Return data
  const response = await fetch(`http://${window.location.hostname}:8081${subDirect}`, bodyJsonified);
  const code = response.status;
  const data = (await response.json()) as A;
  return { code, data };
}

/**
 * Synchronous single get request.
 */
export async function get<A extends object>(subDirect: string) {
  // Initialize
  const bodyJsonified = {
    method: "GET",
  };

  // Shot request & Return data
  const response = await fetch(`http://${window.location.hostname}:8081${subDirect}`, bodyJsonified);
  const code = response.status;
  const data = (await response.json()) as A;
  return { code, data };
}
