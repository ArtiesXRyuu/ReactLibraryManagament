export const oktaConfig = {
    clientId: 'jK9gAnooth6NDmuBqVG0sLptJKlG9PEB',
    issuer: 'https://dev-jv6g3680idczmgyy.us.auth0.com',
    redirectUri: 'https://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsCheck: true,
}