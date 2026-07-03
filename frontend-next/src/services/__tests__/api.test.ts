import { fetchApi, getToken, setToken, removeToken } from '../api';

describe('API Service', () => {
  beforeEach(() => {
    // Clear localStorage before each test
    window.localStorage.clear();
    // Clear fetch mocks
    global.fetch = jest.fn();
  });

  describe('Token management', () => {
    it('should set and get token from localStorage', () => {
      setToken('test-token');
      expect(getToken()).toBe('test-token');
      expect(window.localStorage.getItem('jwt_token')).toBe('test-token');
    });

    it('should remove token from localStorage', () => {
      setToken('test-token');
      removeToken();
      expect(getToken()).toBeNull();
      expect(window.localStorage.getItem('jwt_token')).toBeNull();
    });
  });

  describe('fetchApi', () => {
    it('should send token in Authorization header if present', async () => {
      setToken('test-token');
      const mockResponse = { ok: true, headers: new Headers({'content-type': 'application/json'}), json: jest.fn().mockResolvedValue({ data: 'ok' }) };
      (global.fetch as jest.Mock).mockResolvedValue(mockResponse);

      await fetchApi('/test');

      expect(global.fetch).toHaveBeenCalledWith('/api/test', expect.objectContaining({
        headers: expect.objectContaining({
          Authorization: 'Bearer test-token'
        })
      }));
    });

    it('should throw error if response is not ok', async () => {
      const mockResponse = { 
        ok: false, 
        status: 400,
        text: jest.fn().mockResolvedValue('{"message": "Bad Request"}') 
      };
      (global.fetch as jest.Mock).mockResolvedValue(mockResponse);

      await expect(fetchApi('/test')).rejects.toThrow('{"message": "Bad Request"}');
    });
  });
});
