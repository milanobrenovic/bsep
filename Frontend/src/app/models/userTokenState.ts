export class UserTokenState {

  public jwtAccessToken: string;
  public expiresIn: number;

	constructor($jwtAccessToken: string, $expiresIn: number) {
		this.jwtAccessToken = $jwtAccessToken;
		this.expiresIn = $expiresIn;
	}

}