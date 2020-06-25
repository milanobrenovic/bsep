import { UserTokenState } from './userTokenState';

export class LoggedInUser {

	public id: number;
	public username: string;
	public role: string;
	public userTokenState: UserTokenState;
	
	constructor($id: number, $username: string, $role: string, $userTokenState: UserTokenState) {
		this.id = $id;
		this.username = $username;
		this.role = $role;
		this.userTokenState = $userTokenState;
	}

}