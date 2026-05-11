export interface ApiResponse<T> {
  statusCode: string;
  message: string;
  data: T;
}
