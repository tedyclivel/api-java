import { render, screen, fireEvent } from '@testing-library/react';
import Navbar from '../Navbar';
import { getToken, removeToken } from '@/services/api';
import { useRouter, usePathname } from 'next/navigation';

// Mock next/navigation
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(),
  usePathname: jest.fn(),
}));

// Mock api service
jest.mock('@/services/api', () => ({
  getToken: jest.fn(),
  removeToken: jest.fn(),
}));

describe('Navbar Component', () => {
  const mockPush = jest.fn();

  beforeEach(() => {
    (useRouter as jest.Mock).mockReturnValue({ push: mockPush });
    (usePathname as jest.Mock).mockReturnValue('/');
    jest.clearAllMocks();
  });

  it('renders correctly when not authenticated', () => {
    (getToken as jest.Mock).mockReturnValue(null);
    render(<Navbar />);

    expect(screen.getByText('Connexion')).toBeInTheDocument();
    expect(screen.getByText('Créer un compte')).toBeInTheDocument();
    expect(screen.queryByText('Tableau de bord')).not.toBeInTheDocument();
  });

  it('renders correctly when authenticated', () => {
    (getToken as jest.Mock).mockReturnValue('valid-token');
    render(<Navbar />);

    expect(screen.getByText('Tableau de bord')).toBeInTheDocument();
    expect(screen.getByText('Déconnexion')).toBeInTheDocument();
    expect(screen.queryByText('Connexion')).not.toBeInTheDocument();
  });

  it('handles logout correctly', () => {
    (getToken as jest.Mock).mockReturnValue('valid-token');
    render(<Navbar />);

    const logoutButton = screen.getByText('Déconnexion');
    fireEvent.click(logoutButton);

    expect(removeToken).toHaveBeenCalled();
    expect(mockPush).toHaveBeenCalledWith('/login');
  });
});
