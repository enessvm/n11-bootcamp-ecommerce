import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { z } from 'zod';
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
  label: z.string().max(50, 'Max 50 characters').optional().or(z.literal('')),
  recipientName: z.string().min(1, 'Required').max(100),
  phoneNumber: z
    .string()
    .regex(/^\+?[0-9 ]{7,20}$/, '7-20 digits, optionally prefixed with +'),
  line1: z.string().min(1, 'Required').max(200),
  line2: z.string().max(200).optional().or(z.literal('')),
  city: z.string().min(1, 'Required').max(100),
  postalCode: z.string().min(1, 'Required').max(20),
  country: z.string().min(1, 'Required').max(100),
  isDefault: z.boolean(),
});

const EMPTY_DEFAULTS = {
  label: '',
  recipientName: '',
  phoneNumber: '',
  line1: '',
  line2: '',
  city: '',
  postalCode: '',
  country: 'Türkiye',
  isDefault: false,
};

/**
 * Create-or-edit address form. Pass `defaultValues` (an AddressResponse) for edit mode.
 */
export function AddressForm({ defaultValues, onSubmit, onCancel, busy, submitLabel }) {
  const form = useForm({
    resolver: zodResolver(schema),
    defaultValues: { ...EMPTY_DEFAULTS, ...defaultValues },
    mode: 'onBlur',
  });

  const handleSubmit = async (values) => {
    const payload = {
      ...values,
      label: values.label || undefined,
      line2: values.line2 || undefined,
    };
    await onSubmit(payload);
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="label"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Label (optional)</FormLabel>
              <FormControl>
                <Input placeholder="Home, Work, ..." {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
          <FormField
            control={form.control}
            name="recipientName"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Recipient name</FormLabel>
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
        </div>

        <FormField
          control={form.control}
          name="line1"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Address line 1</FormLabel>
              <FormControl>
                <Input
                  autoComplete="address-line1"
                  placeholder="Street and number"
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />
        <FormField
          control={form.control}
          name="line2"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Address line 2 (optional)</FormLabel>
              <FormControl>
                <Input
                  autoComplete="address-line2"
                  placeholder="Apartment, suite, district"
                  {...field}
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="grid grid-cols-1 gap-4 sm:grid-cols-3">
          <FormField
            control={form.control}
            name="postalCode"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Postal code</FormLabel>
                <FormControl>
                  <Input autoComplete="postal-code" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="city"
            render={({ field }) => (
              <FormItem>
                <FormLabel>City</FormLabel>
                <FormControl>
                  <Input autoComplete="address-level2" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
          <FormField
            control={form.control}
            name="country"
            render={({ field }) => (
              <FormItem>
                <FormLabel>Country</FormLabel>
                <FormControl>
                  <Input autoComplete="country-name" {...field} />
                </FormControl>
                <FormMessage />
              </FormItem>
            )}
          />
        </div>

        <FormField
          control={form.control}
          name="isDefault"
          render={({ field }) => (
            <FormItem className="flex flex-row items-center gap-2 space-y-0">
              <FormControl>
                <input
                  id={field.name}
                  type="checkbox"
                  checked={field.value}
                  onChange={(e) => field.onChange(e.target.checked)}
                  className="h-4 w-4 rounded border-input text-primary focus:ring-2 focus:ring-ring"
                />
              </FormControl>
              <FormLabel htmlFor={field.name} className="text-sm font-normal">
                Set as default address
              </FormLabel>
            </FormItem>
          )}
        />

        <div className="flex justify-end gap-2 pt-2">
          {onCancel && (
            <Button type="button" variant="ghost" onClick={onCancel} disabled={busy}>
              Cancel
            </Button>
          )}
          <Button type="submit" disabled={busy}>
            {busy ? 'Saving...' : (submitLabel ?? 'Save address')}
          </Button>
        </div>
      </form>
    </Form>
  );
}
