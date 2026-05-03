import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
import { Pencil } from 'lucide-react';
import { useAuth } from '@/auth/useAuth';
import { useUpdateMe } from '@/hooks/useUpdateMe';
import { Button } from '@/components/ui/button';
import { Input } from '@/components/ui/input';
import {
  Form,
  FormControl,
  FormField,
  FormItem,
  FormLabel,
  FormMessage,
} from '@/components/ui/form';

const schema = z.object({
  displayName: z
    .string()
    .min(1, 'Required')
    .max(100, 'Max 100 characters'),
  phoneNumber: z
    .string()
    .regex(/^\+?[0-9 ]{7,20}$/, '7-20 digits, optionally prefixed with +'),
  identityNumber: z
    .string()
    .regex(/^\d{11}$/, 'Must be exactly 11 digits'),
});

export function AccountProfilePage() {
  const { user } = useAuth();
  const update = useUpdateMe();
  const [editing, setEditing] = useState(false);

  const form = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      displayName: user?.displayName ?? '',
      phoneNumber: user?.phoneNumber ?? '',
      identityNumber: user?.identityNumber ?? '',
    },
    mode: 'onBlur',
  });

  if (!user) return null;

  const handleEdit = () => {
    form.reset({
      displayName: user.displayName ?? '',
      phoneNumber: user.phoneNumber ?? '',
      identityNumber: user.identityNumber ?? '',
    });
    setEditing(true);
  };

  const handleCancel = () => {
    setEditing(false);
  };

  const onSubmit = async (values) => {
    await update.mutateAsync(values);
    setEditing(false);
  };

  return (
    <div className="rounded-lg border bg-card p-6 shadow-sm">
      <div className="flex items-baseline justify-between">
        <h2 className="text-lg font-semibold">Profile</h2>
        {!editing && (
          <Button variant="outline" size="sm" onClick={handleEdit}>
            <Pencil className="mr-2 h-4 w-4" /> Edit
          </Button>
        )}
      </div>

      {editing ? (
        <Form {...form}>
          <form onSubmit={form.handleSubmit(onSubmit)} className="mt-6 space-y-4">
            <FormField
              control={form.control}
              name="displayName"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Display name</FormLabel>
                  <FormControl>
                    <Input autoComplete="name" {...field} />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="phoneNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Phone number</FormLabel>
                  <FormControl>
                    <Input
                      type="tel"
                      autoComplete="tel"
                      placeholder="+90 555 123 45 67"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />
            <FormField
              control={form.control}
              name="identityNumber"
              render={({ field }) => (
                <FormItem>
                  <FormLabel>Identity number</FormLabel>
                  <FormControl>
                    <Input
                      inputMode="numeric"
                      maxLength={11}
                      placeholder="11-digit TR identity number"
                      {...field}
                    />
                  </FormControl>
                  <FormMessage />
                </FormItem>
              )}
            />

            <div className="flex justify-end gap-2 pt-2">
              <Button
                type="button"
                variant="ghost"
                onClick={handleCancel}
                disabled={update.isPending}
              >
                Cancel
              </Button>
              <Button type="submit" disabled={update.isPending}>
                {update.isPending ? 'Saving...' : 'Save changes'}
              </Button>
            </div>
          </form>
        </Form>
      ) : (
        <dl className="mt-6 grid grid-cols-1 gap-4 sm:grid-cols-2">
          <Field label="Display name" value={user.displayName} />
          <Field label="Phone number" value={user.phoneNumber} />
          <Field label="Identity number" value={user.identityNumber} />
        </dl>
      )}
    </div>
  );
}

function Field({ label, value, className }) {
  return (
    <div>
      <dt className="text-xs font-medium uppercase tracking-wide text-muted-foreground">
        {label}
      </dt>
      <dd className={`mt-1 text-sm text-foreground ${className ?? ''}`}>
        {value || <span className="text-muted-foreground">Not set</span>}
      </dd>
    </div>
  );
}
