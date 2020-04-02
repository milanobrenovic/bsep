export class UserTokenState {

  jwtAccessToken: string;
  expiresIn: number;

  constructor(jwtAccessToken: string, expiresIn: number) {
    this.jwtAccessToken = jwtAccessToken;
    this.expiresIn = expiresIn;
  }

}