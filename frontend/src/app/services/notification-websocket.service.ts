import { Injectable } from '@angular/core';
import { Subject } from 'rxjs';
import { Client, IMessage } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs';
import { NotificationResponse } from '../core/models/notification.model';

@Injectable({
  providedIn: 'root',
})
export class NotificationWebSocketService {
  private client: Client | null = null;

  private notificationSubject = new Subject<NotificationResponse>();

  notifications$ = this.notificationSubject.asObservable();

  connect(token: string): void {
    if (this.client?.active) {
      return;
    }

    this.client = new Client({
      reconnectDelay: 5000,

      webSocketFactory: () => new SockJS('http://localhost:8080/ws'),

      connectHeaders: {
        Authorization: `Bearer ${token}`,
      },

      debug: (str) => {
        console.log('[STOMP]', str);
      },
    });

    this.client.onConnect = () => {
      console.log('WebSocket connected');

      this.client?.subscribe(
        '/user/queue/notifications',
        (message: IMessage) => {
          try {
            const notification: NotificationResponse = JSON.parse(message.body);

            console.log('Notification received', notification);

            this.notificationSubject.next(notification);
          } catch (err) {
            console.error('Failed to parse notification', err);
          }
        },
      );
    };

    this.client.onStompError = (frame) => {
      console.error('STOMP Error', frame);
    };

    this.client.onWebSocketError = (event) => {
      console.error('WebSocket Error', event);
    };

    this.client.onDisconnect = () => {
      console.log('WebSocket disconnected');
    };

    this.client.activate();
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }
}
