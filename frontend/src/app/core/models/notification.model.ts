import { NotificationType } from './enums.model';

export interface NotificationResponse {
  id: number;
  title: string;
  message: string;
  type: NotificationType;
  isRead: boolean;
  referenceId: number;
  createdAt: string;
}
