import { Moment } from 'moment';
import { IUser } from 'app/shared/model/user.model';

export interface INotification {
  id?: number;
  haveRead?: boolean;
  date?: Moment;
  message?: string;
  recipient?: IUser;
}

export const defaultValue: Readonly<INotification> = {
  haveRead: false
};
