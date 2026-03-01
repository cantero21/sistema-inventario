import { ApplicationConfig, provideZoneChangeDetection, importProvidersFrom } from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './interceptors/auth.interceptors';
import {
  LucideAngularModule,
  LayoutDashboard, Package, FolderOpen, Users, Truck,
  FileText, ArrowRightLeft, Settings, LogOut, Box,
  TrendingUp, ShoppingCart, UserCheck, ClipboardList,
  Plus, Pencil, Trash2, X, Save,Ban,ArrowDownToLine, ArrowUpFromLine , Shield , Menu,
  Mail, Lock, User, Eye, EyeOff , ShoppingBag
} from 'lucide-angular';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor])),
    importProvidersFrom(
      LucideAngularModule.pick({
    LayoutDashboard, Package, FolderOpen, Users, Truck,
    FileText, ArrowRightLeft, Settings, LogOut, Box,
    TrendingUp, ShoppingCart, UserCheck, ClipboardList,
    Plus, Pencil, Trash2, X, Save, Ban , ArrowDownToLine, ArrowUpFromLine , Shield, Menu,
    Mail, Lock, User, Eye, EyeOff , ShoppingBag
      })
    )
  ]
};
