import axios from 'axios';
import { ICrudDeleteAction, ICrudGetAction, ICrudGetAllAction, ICrudPutAction, loadMoreDataWhenScrolled, parseHeaderForLinks } from 'react-jhipster';

import { cleanEntity } from 'app/shared/util/entity-utils';
import { FAILURE, REQUEST, SUCCESS } from 'app/shared/reducers/action-type.util';

import { defaultValue, INotification } from 'app/shared/model/notification.model';

export const ACTION_TYPES = {
  FETCH_NOTIFICATION_LIST: 'notification/FETCH_NOTIFICATION_LIST',
  FETCH_NOTIFICATION: 'notification/FETCH_NOTIFICATION',
  CREATE_NOTIFICATION: 'notification/CREATE_NOTIFICATION',
  UPDATE_NOTIFICATION: 'notification/UPDATE_NOTIFICATION',
  DELETE_NOTIFICATION: 'notification/DELETE_NOTIFICATION',
  RESET: 'notification/RESET'
};

const initialState = {
  loading: false,
  errorMessage: null,
  entities: [] as ReadonlyArray<INotification>,
  entity: defaultValue,
  links: { next: 0 },
  updating: false,
  totalItems: 0,
  updateSuccess: false
};

export type NotificationState = Readonly<typeof initialState>;

// Reducer

export default (state: NotificationState = initialState, action): NotificationState => {
  switch (action.type) {
    case REQUEST(ACTION_TYPES.FETCH_NOTIFICATION_LIST):
    case REQUEST(ACTION_TYPES.FETCH_NOTIFICATION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        loading: true
      };
    case REQUEST(ACTION_TYPES.CREATE_NOTIFICATION):
    case REQUEST(ACTION_TYPES.UPDATE_NOTIFICATION):
    case REQUEST(ACTION_TYPES.DELETE_NOTIFICATION):
      return {
        ...state,
        errorMessage: null,
        updateSuccess: false,
        updating: true
      };
    case FAILURE(ACTION_TYPES.FETCH_NOTIFICATION_LIST):
    case FAILURE(ACTION_TYPES.FETCH_NOTIFICATION):
    case FAILURE(ACTION_TYPES.CREATE_NOTIFICATION):
    case FAILURE(ACTION_TYPES.UPDATE_NOTIFICATION):
    case FAILURE(ACTION_TYPES.DELETE_NOTIFICATION):
      return {
        ...state,
        loading: false,
        updating: false,
        updateSuccess: false,
        errorMessage: action.payload
      };
    case SUCCESS(ACTION_TYPES.FETCH_NOTIFICATION_LIST):
      const links = parseHeaderForLinks(action.payload.headers.link);
      return {
        ...state,
        links,
        loading: false,
        totalItems: action.payload.headers['x-total-count'],
        entities: loadMoreDataWhenScrolled(state.entities, action.payload.data, links)
      };
    case SUCCESS(ACTION_TYPES.FETCH_NOTIFICATION):
      return {
        ...state,
        loading: false,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.CREATE_NOTIFICATION):
    case SUCCESS(ACTION_TYPES.UPDATE_NOTIFICATION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: action.payload.data
      };
    case SUCCESS(ACTION_TYPES.DELETE_NOTIFICATION):
      return {
        ...state,
        updating: false,
        updateSuccess: true,
        entity: {}
      };
    case ACTION_TYPES.RESET:
      return {
        ...initialState
      };
    default:
      return state;
  }
};

const apiUrl = 'api/notifications';

// Actions

export const getEntities: ICrudGetAllAction<INotification> = (page, size, sort) => {
  const requestUrl = `${apiUrl}${sort ? `?page=${page}&size=${size}&sort=${sort}` : ''}`;
  return {
    type: ACTION_TYPES.FETCH_NOTIFICATION_LIST,
    payload: axios.get<INotification>(`${requestUrl}${sort ? '&' : '?'}cacheBuster=${new Date().getTime()}`)
  };
};

export const getEntity: ICrudGetAction<INotification> = id => {
  const requestUrl = `${apiUrl}/${id}`;
  return {
    type: ACTION_TYPES.FETCH_NOTIFICATION,
    payload: axios.get<INotification>(requestUrl)
  };
};

export const createEntity: ICrudPutAction<INotification> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.CREATE_NOTIFICATION,
    payload: axios.post(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const updateEntity: ICrudPutAction<INotification> = entity => async dispatch => {
  const result = await dispatch({
    type: ACTION_TYPES.UPDATE_NOTIFICATION,
    payload: axios.put(apiUrl, cleanEntity(entity))
  });
  return result;
};

export const deleteEntity: ICrudDeleteAction<INotification> = id => async dispatch => {
  const requestUrl = `${apiUrl}/${id}`;
  const result = await dispatch({
    type: ACTION_TYPES.DELETE_NOTIFICATION,
    payload: axios.delete(requestUrl)
  });
  return result;
};

export const reset = () => ({
  type: ACTION_TYPES.RESET
});
